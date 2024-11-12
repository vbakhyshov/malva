package thu.malva.ui.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import thu.malva.databinding.FragmentReservationBinding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.Spinner
import thu.malva.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationFragment : Fragment() {
    private val GUEST_PREFS = "guestPrefs"
    private val IS_GUEST = "isGuest"

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var dateTimeTextView: TextView
    private lateinit var peopleCountSpinner: Spinner
    private val calendar = Calendar.getInstance()
    private var selectedTable: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("Reservations")
        sharedPreferences = requireActivity().getSharedPreferences(GUEST_PREFS, Context.MODE_PRIVATE)

        dateTimeTextView = binding.datetimeInput
        peopleCountSpinner = binding.peopleCountSpinner

        // Check for authentication and guest status
        if (isUserLoggedIn()) {
            // Authorized or regular user view
            setupReservationContent()
        } else {
            // Guest view with login prompt
            setupGuestView()
        }

        return binding.root
    }

    private fun isUserLoggedIn(): Boolean {
        // Check if Firebase authentication is available or if the user is not marked as a guest
        val isGuest = sharedPreferences.getBoolean(IS_GUEST, false)
        return auth.currentUser != null && !isGuest
    }

    private fun setupReservationContent() {
        binding.guestView.visibility = View.GONE
        binding.bookingContent.visibility = View.VISIBLE
        setupTableSelection()
        setupPeopleCountOptions(1, 2)

        binding.submitReservationButton.setOnClickListener {
            submitReservation()
        }
    }

    private fun setupGuestView() {
        binding.guestView.visibility = View.VISIBLE
        binding.bookingContent.visibility = View.GONE
        binding.login2Button.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_reservation_to_login_fragment)
        }
        binding.signup2Button.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_reservation_to_signup_fragment)
        }
    }

    private fun setupTableSelection() {
        val tableButtons = listOf(
            binding.table1, binding.table2, binding.table3, binding.table4, binding.table5,
            binding.table6, binding.table7, binding.table8, binding.table9, binding.table10,
            binding.table11, binding.table12, binding.table13, binding.table14, binding.table15,
            binding.table16, binding.table17, binding.table18, binding.table19, binding.table20,
            binding.table21, binding.table22
        )

        tableButtons.forEach { button ->
            button.setOnClickListener {
                onTableSelected(button)
            }
        }
    }

    private fun onTableSelected(button: Button) {
        selectedTable = button.contentDescription.toString()
        binding.selectedTableTextView.text = "You selected $selectedTable"

        val tableNumber = selectedTable?.substringAfter("Table ")?.toIntOrNull()
        if (tableNumber != null) {
            if (tableNumber in 1..15) {
                setupPeopleCountOptions(1, 2)
            } else if (tableNumber in 16..22) {
                setupPeopleCountOptions(3, 4, 5, 6)
            }
        }
    }

    private fun setupPeopleCountOptions(vararg options: Int) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        peopleCountSpinner.adapter = adapter
    }

    private fun submitReservation() {
        val dateTime = dateTimeTextView.text.toString()
        val peopleCount = peopleCountSpinner.selectedItem.toString()
        val preferences = binding.preferencesInput.text.toString()

        if (selectedTable.isNullOrBlank() || dateTime.isBlank() || peopleCount.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve user information from SharedPreferences
        val name = sharedPreferences.getString("name", "") ?: ""
        val surname = sharedPreferences.getString("surname", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""
        val mobile = sharedPreferences.getString("mobile", "") ?: ""
        val dob = sharedPreferences.getString("dob", "") ?: ""

        // Create reservation object with additional user information
        val reservationId = database.push().key ?: return
        val reservation = Reservation(
            table = selectedTable!!,
            dateTime = dateTime,
            peopleCount = peopleCount,
            preferences = preferences,
            name = name,
            surname = surname,
            email = email,
            mobile = mobile,
            dob = dob
        )

        // Save the reservation to the database
        database.child(reservationId).setValue(reservation).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Reservation submitted", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(context, "Failed to submit reservation", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTimeTextView.setOnClickListener { showDateTimePicker() }
    }

    private fun showDateTimePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                dateTimeTextView.text = dateFormat.format(calendar.time)
            }

            TimePickerDialog(
                requireContext(),
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun clearFields() {
        selectedTable = null
        binding.selectedTableTextView.text = ""
        dateTimeTextView.text = ""
        binding.preferencesInput.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Data class for Reservation data
data class Reservation(
    val table: String = "",
    val dateTime: String = "",
    val peopleCount: String = "",
    val preferences: String = "",
    val name: String = "",        // Additional fields
    val surname: String = "",
    val email: String = "",
    val mobile: String = "",
    val dob: String = ""
)
