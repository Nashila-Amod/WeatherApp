package android.training.weather.city

import android.os.Bundle
import android.training.weather.App
import android.training.weather.Database
import android.training.weather.R
import android.training.weather.utils.toast
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CityFragment : Fragment(), CityAdapter.CityItemListener {

    interface CityFragmentListener
    {
        fun onCitySelected(city: City)
        fun onEmptyCities()
    }

    var listener : CityFragmentListener? = null

    private lateinit var cities : MutableList<City>
    private lateinit var database : Database

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = App.database
        setHasOptionsMenu(true)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_city, container, false)
        recyclerView = view.findViewById(R.id.cities_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cities = database.getAllCities()

        adapter = CityAdapter(cities, this)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_city, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.action_create_city -> {
                showCreateCityDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun selectFirstCity()
    {
        when(cities.isEmpty())
        {
            true -> listener?.onEmptyCities()
            false -> onCitySelected(cities.first())
        }
    }

    private fun showCreateCityDialog() {

        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListener{

            override fun onDialogPositiveClick(cityName: String) {

                saveCity(City(cityName))

            }

            override fun onDialogNegativeClick() {}

        }

        createCityFragment.show(fragmentManager!!, "CreateCityDialogFragment")

    }

    private fun saveCity(city: City) {

       if(database.createCity(city))
       {
            cities.add(city)
            adapter.notifyDataSetChanged()
       }
       else
       {
           context!!.toast(getString(R.string.city_message_error_could_not_create_city))
       }

    }

    private fun showDeleteCityDialog(city: City) {

        val deleteCityFragment = DeleteCityDialogFragment.newInstance(city.name)

        deleteCityFragment.listener = object : DeleteCityDialogFragment.DeleteCityDialogListener
        {
            override fun onDialogPositiveClick() {
                deleteCity(city)
            }

            override fun onDialogNegativeClick() {}

        }

        deleteCityFragment.show(fragmentManager!!, "DeleteCityDialogFragment")

    }

    private fun deleteCity(city: City) {

        if(database.deleteCity(city))
        {
            cities.remove(city)
            adapter.notifyDataSetChanged()
            selectFirstCity()
            context!!.toast(getString(R.string.city_message_info_city_deleted, city.name))
        }
        else
        {
            context!!.toast(getString(R.string.city_message_error_could_not_delete_city, city.name))

        }

    }

    override fun onCitySelected(city: City) {
        listener?.onCitySelected(city)
    }

    override fun onCityDeleted(city: City) {
        showDeleteCityDialog(city)
    }

}