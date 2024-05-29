package com.ajou.helpt.train.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ajou.helpt.train.view.TrainingImageFragment
import com.ajou.helpt.train.view.TrainingYoutubeFragment

class TrainingViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf<Fragment>(
        TrainingImageFragment(),
        TrainingYoutubeFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}