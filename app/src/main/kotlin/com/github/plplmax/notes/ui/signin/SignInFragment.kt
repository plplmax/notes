/*
 * MIT License
 *
 * Copyright (c) 2022 Maksim Ploski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.plplmax.notes.ui.signin

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentSignInBinding
import com.github.plplmax.notes.domain.core.Result
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private var signUpScreenListener: ToSignUpScreenListener? = null

    private val viewModel: SignInViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ToSignUpScreenListener) {
            signUpScreenListener = context
        } else {
            throw IllegalArgumentException("Context must implement ToSignUpScreenListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextView()
        setupToolbar()
        observeToolbarBackButton()
        observeInputs()
        observeEmailError()
        observePasswordError()
        observeSignInButton()
        observeSignInResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        signUpScreenListener = null
    }

    private fun initTextView() {
        val fullText = getString(R.string.dont_have_account)
        val signUp = getString(R.string.sign_up)

        val spannableString = SpannableString(fullText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                signUpScreenListener?.navigateToSignUpScreen()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            fullText.indexOf(signUp),
            fullText.indexOf(signUp) + signUp.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.dontHaveAccount.run {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setupToolbar() {
        binding.includeToolbar.toolbar.run {
            title = getString(R.string.app_name)
            navigationIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_chevron_left_24
            )
            navigationContentDescription = "Back"
        }
    }

    private fun observeToolbarBackButton() {
        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            signUpScreenListener?.navigateToSignUpScreen()
        }
    }

    private fun observeInputs() {
        binding.emailInput.doOnTextChanged(disabledErrorAction(binding.emailInputLayout))
        binding.passwordInput.doOnTextChanged(disabledErrorAction(binding.passwordInputLayout))
    }

    private fun disabledErrorAction(inputLayout: TextInputLayout) =
        { _: CharSequence?, _: Int, _: Int, _: Int -> inputLayout.error = null }

    private fun observeSignInButton() {
        binding.signInButton.setOnClickListener {
            viewModel.authUser(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString()
            )
        }
    }

    private fun observeEmailError() {
        viewModel.emailErrorLiveData.observe(viewLifecycleOwner, binding.emailInputLayout::setError)
    }

    private fun observePasswordError() {
        viewModel.passwordErrorLiveData.observe(
            viewLifecycleOwner,
            binding.passwordInputLayout::setError
        )
    }

    private fun observeSignInResult() {
        viewModel.signInResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    Snackbar.make(
                        binding.signInButton,
                        "success",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is Result.Fail -> {
                    Snackbar.make(
                        binding.signInButton,
                        it.e,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    unblockForm()
                }
                is Result.Loading -> blockForm()
            }
        }
    }

    private fun unblockForm() {
        binding.emailInput.isEnabled = true
        binding.passwordInput.isEnabled = true
        binding.signInButton.isEnabled = true
    }

    private fun blockForm() {
        binding.emailInput.isEnabled = false
        binding.passwordInput.isEnabled = false
        binding.signInButton.isEnabled = false
    }

    interface ToSignUpScreenListener {
        fun navigateToSignUpScreen()
    }
}