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

package com.github.plplmax.notes.ui.signup

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentSignUpBinding
import com.github.plplmax.notes.domain.core.Result
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var signInScreenListener: ToSignInScreenListener? = null

    private val viewModel: SignUpViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ToSignInScreenListener) {
            signInScreenListener = context
        } else {
            throw Exception("Context must implement ToSignInScreenListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextView()
        observeSignUpResult()
        observeSignUpButton()
        observeInputs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        signInScreenListener = null
    }

    private fun initTextView() {
        val fullText = getString(R.string.already_have_an_account_sign_in)
        val signIn = getString(R.string.sign_in)

        val spannableString = SpannableString(fullText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                signInScreenListener?.navigateToSignInScreen()
            }
        }

        spannableString.setSpan(
            clickableSpan,
            fullText.indexOf(signIn),
            fullText.indexOf(signIn) + signIn.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.alreadyHaveAccount.run {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun observeSignUpResult() {
        viewModel.state.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Snackbar.make(
                        binding.signUpButton,
                        user.data.email,
                        Snackbar.LENGTH_LONG
                    ).show()
                    enableSignUpForm()
                }
                is Result.Fail -> {
                    Snackbar.make(
                        binding.signUpButton,
                        user.e,
                        Snackbar.LENGTH_LONG
                    ).show()
                    enableSignUpForm()
                }
                is Result.Loading -> disableSignUpForm()
            }
        }
    }

    private fun observeSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val email = inputContent(binding.emailInput)
            val password = inputContent(binding.passwordInput)
            val repeatPassword = inputContent(binding.repeatPasswordInput)

            binding.emailInputLayout.error =
                if (viewModel.validEmail(email)) null else getString(R.string.invalid_email)

            val validationResult = viewModel.validPassword(password)

            binding.passwordInputLayout.error =
                when (validationResult) {
                    is Result.Fail -> validationResult.e
                    else -> null
                }

            binding.repeatPasswordInputLayout.error =
                if (viewModel.validRepeatPassword(
                        password,
                        repeatPassword
                    )
                ) null else getString(R.string.password_is_not_the_same)

            if (!errorsExist()) viewModel.createUser(email, password)
        }
    }

    private fun inputContent(input: TextInputEditText) = input.text?.toString() ?: ""

    private fun errorsExist() = binding.emailInputLayout.error != null ||
            binding.passwordInputLayout.error != null ||
            binding.repeatPasswordInputLayout.error != null

    private fun observeInputs() {
        binding.emailInput.doOnTextChanged(disabledErrorAction(binding.emailInputLayout))
        binding.passwordInput.doOnTextChanged(disabledErrorAction(binding.passwordInputLayout))
        binding.repeatPasswordInput.doOnTextChanged(disabledErrorAction(binding.repeatPasswordInputLayout))
    }

    private fun disabledErrorAction(inputLayout: TextInputLayout) =
        { _: CharSequence?, _: Int, _: Int, _: Int -> inputLayout.error = null }

    private fun disableSignUpForm() = changeSignUpFormState(newState = false)

    private fun enableSignUpForm() = changeSignUpFormState(newState = true)

    private fun changeSignUpFormState(newState: Boolean) {
        binding.emailInput.isEnabled = newState
        binding.passwordInput.isEnabled = newState
        binding.repeatPasswordInput.isEnabled = newState
        binding.signUpButton.isEnabled = newState
    }

    interface ToSignInScreenListener {
        fun navigateToSignInScreen()
    }
}