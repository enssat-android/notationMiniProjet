package fr.enssat.notationminiprojet
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Intent
import com.google.gson.*
import android.widget.Toast

class NoteViewModel(val context: Context): ViewModel() {
    companion object {
        val MAX_ITEMS = 9
        val gson = GsonBuilder().create()
    }

    private var _setOfResultats: MutableSet<Resultat> = mutableSetOf()
    private var _teamMembers: MutableSet<String> = mutableSetOf()
    private var _list:MutableList<String> = mutableListOf()
    private val  _readytoSend = MutableLiveData<Boolean>(false)

    val readytoSend : LiveData<Boolean> get() = _readytoSend

    fun notation(item:String, note: Int?) {
        if (note != null) {
            _setOfResultats = _setOfResultats.filterNot{elem -> elem.item == item}.toMutableSet()
            _setOfResultats.add(Resultat(item,note))
            isReady()
        }

    }

    fun registerMember(member:String?) {
        if (member != null) {
            _teamMembers = _teamMembers.filterNot { elem -> elem == member}.toMutableSet()
            _teamMembers.add(member)
        }
    }

    fun resetMembers() {
            _teamMembers.clear()
    }

    fun resetAll() {
        _setOfResultats.clear()
        _teamMembers.clear()
        isReady()
    }


    fun addEmail(mail:String){
        _list.add(mail)
    }

    fun getEmails():String {
        var sb = StringBuffer()
        _list.forEach { sb.append(it)}
        return sb.toString()
    }

    fun resetEmails()
    {   _list.clear()}

    fun sendEmail(){
        if (_teamMembers.size > 0) {
            if (_list.size > 0) {
                val subject = _teamMembers.joinToString(
                    separator = ", ",
                    prefix = "Mini projet notes de ",
                    postfix = "",
                    limit = 3
                )

                val Notes = gson.toJson(_setOfResultats)

                val ems = _list.toTypedArray()
                var intent = Intent(Intent.ACTION_SEND)
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "$subject")
                intent.putExtra(Intent.EXTRA_TEXT, "$Notes")
                intent.putExtra(Intent.EXTRA_EMAIL, ems)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {

                    context.startActivity(intent)
                } catch (ex: android.content.ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No email client found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "No email set.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "No team member set.",
                Toast.LENGTH_SHORT
            ).show()
        }
   }

    private fun isReady(){
        _readytoSend.postValue(_setOfResultats.size == MAX_ITEMS)
    }

    data class Resultat(val item:String, val note:Int) {}
}