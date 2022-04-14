package com.daon.usedtrade_part3_06.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.daon.usedtrade_part3_06.R
import com.daon.usedtrade_part3_06.databinding.FragmentMypageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private val auth : FirebaseAuth by lazy { Firebase.auth }
    private var myPageBinding : FragmentMypageBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = FragmentMypageBinding.bind(view)
        myPageBinding = fragmentMyPageBinding

        // 회원가입 버튼
        myPageBinding?.signUpButton?.setOnClickListener {
            myPageBinding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                // 로그인 중일 때 -> 데이터 변경
                if ( auth.currentUser != null) {
                    auth.currentUser?.updatePassword(password)!!
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Snackbar.make(view, "비밀번호가 변경되었습니다.", Snackbar.LENGTH_SHORT).show()
                            } else {
                                Snackbar.make(view, "비밀번호를 변경하는데 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // 회원가입
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) {
                            if (it.isSuccessful) {
                                Snackbar.make(view, "회원가입에 성공하였습니다.", Snackbar.LENGTH_SHORT).show()
                            } else {
                                Snackbar.make(view, "회원가입에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        // 로그인 버튼
        myPageBinding?.signInOutButton?.setOnClickListener {
            activity?.let {
                myPageBinding?.let { binding ->
                    val email = binding.emailEditText.text.toString()
                    val password = binding.passwordEditText.text.toString()

                    // 로그인 중일 때 -> 로그아웃
                    if (auth.currentUser != null) {
                        auth.signOut()
                        binding.emailEditText.text.clear()
                        binding.emailEditText.isEnabled = true
                        binding.passwordEditText.text.clear()
                        binding.passwordEditText.isEnabled = true

                        binding.signInOutButton.text = "로그인"
                        binding.signInOutButton.isEnabled = false
                        binding.signUpButton.text = "회원가입"
                        binding.signUpButton.isEnabled = false
                    } else {
                        // 로그인
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) {
                                if (it.isSuccessful) {
                                    successSign()
                                } else {
                                    Log.d("MyPageFragment", it.exception.toString())
                                    Snackbar.make(view, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }

        myPageBinding?.emailEditText?.addTextChangedListener {
            myPageBinding?.let { binding ->
                val enable =
                    binding.emailEditText.text.isNotBlank() && binding.passwordEditText.text.isNotBlank()
                binding.signUpButton.isEnabled = enable
                binding.signInOutButton.isEnabled = enable
            }
        }

        myPageBinding?.passwordEditText?.addTextChangedListener {
            myPageBinding?.let { binding ->
                val enable = binding.emailEditText.text.isNotBlank() && binding.passwordEditText.text.isNotBlank()
                binding.signUpButton.isEnabled = enable
                binding.signInOutButton.isEnabled = enable
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 로그인이 되어있지 않다면
        if (auth.currentUser == null) {
            myPageBinding?.apply {
                emailEditText.text.clear()
                passwordEditText.text.clear()
                signInOutButton.text = "로그인"
                signUpButton.text = "회원가입"

                emailEditText.isEnabled = true
                passwordEditText.isEnabled = true
                signInOutButton.isEnabled = false
                signUpButton.isEnabled = false
            }
        } else { // 로그인 중인 경우
            myPageBinding?.apply {
                emailEditText.setText(auth.currentUser?.email)
                emailEditText.isEnabled = false

                signInOutButton.text = "로그아웃"
                signInOutButton.isEnabled = true
                signUpButton.text = "비밀번호 변경"
                signUpButton.isEnabled = true
            }
        }
    }

    private fun successSign () {
        if (auth.currentUser == null) {
            Snackbar.make(requireView(), "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
            return
        }
        Snackbar.make(requireView(), "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT).show()

        myPageBinding?.apply {
            emailEditText.isEnabled = false
            signUpButton.text = "비밀번호 변경"
            signInOutButton.text = "로그아웃"
        }
    }
}