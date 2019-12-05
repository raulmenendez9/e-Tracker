package com.unicomer.e_tracker_test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment


class TerminosFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        //listener?.hideToolBarOnFragmentViewDissapears()

        return inflater.inflate(R.layout.fragment_terminos, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    //ADMIN DEL MENU DE LA TOOLBAR
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menus, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.item_generar).isVisible= false
        menu.findItem(R.id.item_fin_viaje).isVisible= false
        menu.findItem(R.id.action_search).isVisible=false
        menu.findItem(R.id.item_terminos).isVisible=false
    }
    //FIN DEL ADMIN DEL MENU DE LA TOOLBAR

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
        fun showToolBarOnFragmentViewCreate()
        //fun hideToolBarOnFragmentViewDissapears()
    }

    companion object {

        @JvmStatic
        fun newInstance() = TerminosFragment()
    }
}
