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

package com.github.plplmax.notes.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentSignInBinding
import com.github.plplmax.notes.ui.base.BaseAuthFragment
import com.github.plplmax.notes.ui.core.FragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseAuthFragment<FragmentSignInBinding>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttach(context, AuthListener::class.java)
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

        observeToolbarBackButton()
        observeSignInButton()
    }

    override fun initClickableTextView() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                listener?.navigateToSignUpScreen(fromSignIn = true)
            }
        }

        val spannableString = clickableSpannableString(
            R.string.dont_have_account,
            R.string.sign_up,
            clickableSpan
        )

        binding.dontHaveAccount.run {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun setupToolbar() {
        binding.includeToolbar.toolbar.run {
            title = getString(R.string.app_name)
            navigationIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_chevron_left_24
            )
            navigationContentDescription = getString(R.string.back)
        }
    }

    private fun observeToolbarBackButton() {
        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            listener?.navigateToSignUpScreen(fromSignIn = true)
        }
    }

    override fun observeInputs() {
        observeInput(binding.emailInput, binding.emailInputLayout)
        observeInput(binding.passwordInput, binding.passwordInputLayout)
    }

    override fun observeEmailError() {
        observe(viewModel.emailErrorLiveData, binding.emailInputLayout::setError)
    }

    override fun observePasswordError() {
        observe(viewModel.passwordErrorLiveData, binding.passwordInputLayout::setError)
    }

    private fun observeSignInButton() {
        binding.signInButton.setOnClickListener {
            viewModel.authUser(
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString()
            )
        }
    }

    override fun changeFormState(newState: Boolean) {
        binding.emailInput.isEnabled = newState
        binding.passwordInput.isEnabled = newState
        binding.signInButton.isEnabled = newState
    }

    interface ToSignUpScreenListener : FragmentListener {
        fun navigateToSignUpScreen(fromSignIn: Boolean = false)
    }
}