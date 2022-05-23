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

package com.github.plplmax.notes.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.github.plplmax.notes.R
import com.github.plplmax.notes.databinding.FragmentNotesBinding
import com.github.plplmax.notes.ui.base.BaseFragment
import com.github.plplmax.notes.ui.core.FragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : BaseFragment<FragmentNotesBinding, NotesFragment.ToNoteScreenListener>() {

    private val viewModel: NotesViewModel.Base by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupDrawerLayout()
        setupNavigationView()
        observeNotes()
    }

    override fun setupToolbar() {
        binding.includeToolbar.toolbar.run {
            title = getString(R.string.app_name)
            navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_menu_24)
        }
    }

    private fun setupRecyclerView() {
        val manager = GridLayoutManager(requireContext(), 2)

        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = NotesAdapter()
    }

    private fun setupDrawerLayout() {
        val toggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.includeToolbar.toolbar,
            R.string.app_name,
            R.string.app_name
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val scaleFactor = 17f
                val slideX = drawerView.width * (slideOffset / scaleFactor)

                binding.holder.translationX = slideX

                super.onDrawerSlide(drawerView, slideOffset)
            }
        }

        binding.drawerLayout.addDrawerListener(toggle)
    }

    private fun setupNavigationView() {
        binding.navigationView.setCheckedItem(R.id.notes)
    }

    private fun observeNotes() {
        observe(
            viewModel.notesLiveData,
            (binding.recyclerView.adapter as NotesAdapter)::updateNotes
        )
    }

    interface ToNotesScreenListener : FragmentListener {
        fun navigateToNotesScreen()
    }
}