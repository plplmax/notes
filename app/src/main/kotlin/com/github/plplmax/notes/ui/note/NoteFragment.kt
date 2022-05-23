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

package com.github.plplmax.notes.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentNoteBinding
import com.github.plplmax.notes.domain.notes.model.Note
import com.github.plplmax.notes.ui.base.BaseFragment
import com.github.plplmax.notes.ui.core.FragmentListener
import com.github.plplmax.notes.ui.core.hideKeyboard
import com.github.plplmax.notes.ui.core.showKeyboard
import com.github.plplmax.notes.ui.notes.NotesFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding, NotesFragment.ToNotesScreenListener>() {

    private val viewModel: NoteViewModel.Base by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializable(NOTE_KEY)?.let(::restoreNote)
        savedInstanceState?.getSerializable(NOTE_KEY)?.let(::restoreNote)

        arguments ?: binding.noteInput.showKeyboard()
        observeToolbarBackButton()
    }

    override fun onPause() {
        binding.noteInput.hideKeyboard()
        viewModel.submitText(binding.noteInput.text.toString())

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(NOTE_KEY, viewModel.note)

        super.onSaveInstanceState(outState)
    }

    override fun setupToolbar() {
        binding.includeToolbar.toolbar.run {
            title = if (arguments == null) {
                getString(R.string.new_note)
            } else {
                getString(R.string.edit_note)
            }
            navigationIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_chevron_left_24
            )
            navigationContentDescription = getString(R.string.back)
        }
    }

    private fun restoreNote(note: Serializable) {
        viewModel.submitNote(note as Note)
        binding.noteInput.setText(note.text)
    }

    private fun observeToolbarBackButton() {
        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            with(binding.noteInput) {
                if (isFocused) hideKeyboard()
                else requireActivity().onBackPressed()
            }
        }
    }

    companion object {
        private const val NOTE_KEY = "note_key"

        @JvmStatic
        fun newInstance(note: Note): NoteFragment {
            return NoteFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(NOTE_KEY, note)
                }
            }
        }
    }

    interface ToNoteScreenListener : FragmentListener {
        fun navigateToNoteScreenForCreate()
        fun navigateToNoteScreenForEdit(note: Note)
    }
}