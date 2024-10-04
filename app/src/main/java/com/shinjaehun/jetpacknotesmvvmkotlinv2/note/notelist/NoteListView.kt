package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shinjaehun.jetpacknotesmvvmkotlinv2.R
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.startWithFade
import com.shinjaehun.jetpacknotesmvvmkotlinv2.databinding.FragmentNoteListBinding
import com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist.buildlogic.NoteListInjector

private const val TAG = "NoteListView"

class NoteListView : Fragment() {

    private lateinit var binding: FragmentNoteListBinding
    private lateinit var viewModel: NoteListViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_note_list, container, false)
        binding = FragmentNoteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recListFragment.adapter = null
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            NoteListInjector(requireActivity().application).provideNoteListViewModelFactory()
        ).get(NoteListViewModel::class.java)

        (binding.imvSpaceBackground.drawable as AnimationDrawable).startWithFade()

        showLoadingState()
        setUpAdapter()
        observeViewModel()

        binding.fabCreateNewItem.setOnClickListener {
//            val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView("") // 이게 안돼
//            val direction = NoteListViewDirections.actionNoteListViewToNoteDetailView().apply {
//                noteId = ""
//            }
//            findNavController().navigate(direction)
            startNoteDetailWithArgs("")
        }

        binding.imvToolbarAuth.setOnClickListener {
            findNavController().navigate(R.id.loginView)
        }

        viewModel.handleEvent(NoteListEvent.OnStart)
    }

    private fun showLoadingState() = (binding.imvSatelliteAnimation.drawable as AnimationDrawable).start()

    private fun setUpAdapter() {
        adapter = NoteListAdapter()
        adapter.event.observe( // 이게 정확히 어떻게 동작하는지 연구할 필요가 있음...
            viewLifecycleOwner,
            Observer {
                Log.i(TAG, "what is it? $it") // OnNoteItemClick(position=1)
                viewModel.handleEvent(it)
            }
        )
        binding.recListFragment.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.noteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                adapter.submitList(noteList)

                if (noteList.isNotEmpty()) {
                    (binding.imvSatelliteAnimation.drawable as AnimationDrawable).stop()
                    binding.imvSatelliteAnimation.visibility = View.INVISIBLE
                    binding.recListFragment.visibility = View.VISIBLE
                }
            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer { noteId ->
                startNoteDetailWithArgs(noteId)
            }
        )
    }

    private fun showErrorState(errorMessage: String?) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun startNoteDetailWithArgs(nId: String) = findNavController().navigate(
//        NoteListViewDirections.actionNoteListViewToNoteDetailView(noteId)
//        NoteListViewDirections.actionNoteListViewToNoteDetailView().apply {
//            noteId = noteId
//        } // 이건 동작하지 않음(noteId를 다시할당할 수 없다?)

        NoteListViewDirections.actionNoteListViewToNoteDetailView().apply {
            noteId = nId
        } // 이건 동작함
    )
}