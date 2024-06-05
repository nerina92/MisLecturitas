package edu.mis.lecturitas.ui.bookList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.Libro
import org.koin.core.component.KoinComponent

class BookListViewModel: ViewModel(), KoinComponent {



    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: MutableLiveData<Boolean>
        get() = _showProgressBar
    private val _listaLibros = MutableLiveData<ArrayList<Libro>?>(ArrayList())

    val listaLibros: MutableLiveData<ArrayList<Libro>?> = _listaLibros

    private val _openCuento = MutableLiveData<String?>()
    val openCuento: MutableLiveData<String?>
        get() = _openCuento
    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack

    fun consultarLibnos(nivel:Int){
        _showProgressBar.value=true
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("libros")
        val listaAux = ArrayList<Libro>()
        val query = myRef.orderByChild("nivel").equalTo(nivel.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val libro: Libro? = snapshot.getValue(Libro::class.java)
                    if(libro!=null){
                        listaAux.add(libro)
                    }
                }
                _listaLibros.value=listaAux
                _showProgressBar.value=false
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si es necesario
                Log.e("Firebase", "Error al realizar la consulta", databaseError.toException())
            }
        })
    }
    fun setOpenCuentoNull() {
        _openCuento.value = null
    }

    fun backPresed() {
        _goBack.value = true
    }

fun doneGoBack() {
    _goBack.value = false
}
}