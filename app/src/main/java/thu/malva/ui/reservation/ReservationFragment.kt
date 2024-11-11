package thu.malva.ui.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import thu.malva.databinding.FragmentReservationBinding

class ReservationFragment : Fragment() {

    private var _binding: FragmentReservationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val reservationViewModel = ViewModelProvider(this).get(ReservationViewModel::class.java)

        reservationViewModel.text.observe(viewLifecycleOwner) {
            binding.textReservation.text = it
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}