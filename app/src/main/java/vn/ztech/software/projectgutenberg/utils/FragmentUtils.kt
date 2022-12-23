package vn.ztech.software.projectgutenberg.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import vn.ztech.software.projectgutenberg.R

fun addFragment(fragmentManager: FragmentManager, fragment: Fragment) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.apply {
        add(R.id.container, fragment)
        addToBackStack(fragment::class.java.simpleName)
        commit()
    }
}

fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.apply {
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        replace(R.id.container, fragment)
        addToBackStack(null)
        commit()
    }
}

fun popFragment(fragmentManager: FragmentManager, fragment: Fragment) {
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.apply {
        remove(fragment)
        commit()
        fragmentManager.popBackStack()
    }
}
