package com.ajou.helpt.mypage.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentRecordDetailBinding
import com.ajou.helpt.home.adapter.MyPageViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

class RecordDetailFragment : Fragment() {
    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: MyPageViewModel
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setSelectedItem(null)
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[MyPageViewModel::class.java]
        _binding = FragmentRecordDetailBinding.inflate(layoutInflater, container, false)

        val item = viewModel.selectedItem.value!!
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(item.recordDate, format)

        binding.title.text = String.format(getString(R.string.record_detail_title),date.monthValue, date.dayOfMonth)

        binding.comment.text = viewModel.selectedItem.value!!.comment
        binding.name.text = viewModel.selectedItem.value!!.equipmentName
        binding.result.text = String.format(getString(R.string.train_done_result), item.recordTime, item.setNumber, item.count)
        binding.rate.text = String.format(getString(R.string.train_done_percent), item.successRate)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            viewModel.setSelectedItem(null)
            findNavController().popBackStack()
        }

        if (viewModel.selectedItem.value == null) {
            findNavController().popBackStack()
        }

        if (viewModel.selectedItem.value!!.snapshotFile != null) {
            Glide.with(mContext!!)
                .load(viewModel.selectedItem.value!!.snapshotFile)
                .into(binding.img)
        } else {
            binding.downloadBtn.visibility = View.GONE
            binding.imgText.visibility = View.GONE
        }
        binding.downloadBtn.setOnClickListener {
            downloadImage(viewModel.selectedItem.value!!.snapshotFile!!)
        }
    }

    private fun requestPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(mContext!!,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(mContext!!,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            return true
        }

        val permissions: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS)

        ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        return false
    }

    private fun downloadImage(url: String) {
        if (requestPermission()) {
            val fileName =
                "/${getString(R.string.app_name)}/${SimpleDateFormat("yyyyMMddHHmm").format(Date())}.jpg"

            val req = DownloadManager.Request(Uri.parse(url))

            req.setTitle(fileName) // 제목
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // 알림 설정
                .setMimeType("image/*")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    fileName
                ) // 다운로드 완료 시 보여지는 이름

            val manager = mContext!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            manager.enqueue(req)
        } else {
            Log.d("permission check","no permission")
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted = false
                    break
                }
            }
            if (allPermissionsGranted) Log.d("Permission","permission is granted")
            else Log.e("Permission", "permission denied")

        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}