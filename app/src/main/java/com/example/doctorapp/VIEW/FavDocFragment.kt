package com.example.doctorapp.VIEW

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.UTIL.Static.Companion.LOADING
import com.example.doctorapp.UTIL.Static.Companion.USER
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.DocViewCardAdapter
import com.example.doctorapp.databinding.FragmentFavDocBinding
import kotlinx.coroutines.launch

class FavDocFragment : Fragment() {

    lateinit var favDocBinding: FragmentFavDocBinding
    lateinit var adapter: DocViewCardAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favDocBinding = FragmentFavDocBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        var favDoc: ArrayList<DoctorModel> = arrayListOf()

        favDocBinding.ivLottie.setAnimationFromUrl(LOADING)
        val viewmodel = arguments?.getSerializable("viewmodel") as DocViewModel
        favDocBinding.favRecycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            if(isAdded){
                val favDocResult = viewmodel.getFavDoctor(USER!!.uid)
                if (favDocResult.isSuccessful){
                    favDocBinding.main.visibility = View.VISIBLE
                    favDocBinding.load.visibility = View.GONE
                    favDoc = favDocResult.body()!! as ArrayList<DoctorModel>
                    adapter = DocViewCardAdapter(favDoc){
                        callApi(lifecycleScope, favDoc[it],viewmodel,favDoc,it)
                    }
                    favDocBinding.favRecycler.adapter = adapter
                }
            }
        }




        return favDocBinding.root
    }

    private fun callApi(
        lifecycleScope: LifecycleCoroutineScope,
        model: DoctorModel,
        viewmodel: DocViewModel,
        favDoc: ArrayList<DoctorModel>,
        i: Int
    ) {
        lifecycleScope.launch {
            viewmodel.deleteFavDoc(USER!!.uid,model.did)
            favDoc.removeAt(i)
            adapter.update(favDoc)
        }
    }
}