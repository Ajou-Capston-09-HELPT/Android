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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlin.random.Random

class RecordDetailFragment : Fragment() {
    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: MyPageViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[MyPageViewModel::class.java]
        _binding = FragmentRecordDetailBinding.inflate(layoutInflater, container, false)

        val item = viewModel.selectedItem.value!!
        binding.title.text = String.format(getString(R.string.record_detail_title),item.recordDate.month, item.recordDate.dayOfMonth)

        binding.comment.text = viewModel.selectedItem.value!!.comment
        binding.name.text = viewModel.selectedItem.value!!.equipmentName
        binding.result.text = String.format(getString(R.string.train_done_result), item.recordTime, item.setNumber, item.count)
        binding.rate.text = String.format(getString(R.string.train_done_percent), item.successRate)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (viewModel.selectedItem.value == null) {
            findNavController().popBackStack()
        }

//        binding.downloadBtn.setOnClickListener {
//            downloadImgFromUrl(viewModel.selectedItem.value!!.snapshotFile)
//        }
    }

    private fun downloadImgFromUrl(url: String) {
        if (checkPermission()) {
            val fileName =
                "/${getString(R.string.app_name)}/${SimpleDateFormat("yyyyMMddHHmmss").format(viewModel.selectedItem.value!!.recordDate)}.jpg" // 이미지 파일 명


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
        } else requestPermission()
    }

    private fun checkPermission() =
        (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            0
        )
    }
}