package thu.malva.ui.login

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import thu.malva.R
import thu.malva.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        // Toggle password visibility
        binding.showPasswordButton.setOnClickListener {
            togglePasswordVisibility()
        }

        // Handle login button click
        binding.loginButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            binding.progressBar.visibility = View.VISIBLE
            binding.errorText.visibility = View.GONE
            loginViewModel.login(email, password)
        }

        // Handle sign-up button click
        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        // Handle guest login button click
        binding.guestLoginButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            binding.progressBar.visibility = View.GONE
            if (isSuccess) {
                findNavController().navigate(R.id.navigation_home)
            } else {
                binding.errorText.visibility = View.VISIBLE
            }
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            binding.errorText.text = errorMessage
            binding.errorText.visibility = View.VISIBLE
            Log.e("LoginFragment", "Login failed: $errorMessage")
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.showPasswordButton.setImageResource(R.drawable.ic_eye_white_24dp)
        } else {
            binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.showPasswordButton.setImageResource(R.drawable.ic_eye_closed_white_24dp)
        }
        binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
