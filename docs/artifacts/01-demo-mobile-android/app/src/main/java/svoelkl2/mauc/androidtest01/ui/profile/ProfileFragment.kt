package svoelkl2.mauc.androidtest01.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import svoelkl2.mauc.androidtest01.Friend
import svoelkl2.mauc.androidtest01.MainViewModel
import svoelkl2.mauc.androidtest01.R

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var friendAdapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        
        val xpText = root.findViewById<TextView>(R.id.profileXP)
        val nameText = root.findViewById<TextView>(R.id.profileName)
        val birthdayText = root.findViewById<TextView>(R.id.profileBirthday)
        val currencyText = root.findViewById<TextView>(R.id.profileCurrency)
        val seekBar = root.findViewById<SeekBar>(R.id.socialBatterySeekBar)
        val statusText = root.findViewById<TextView>(R.id.socialBatteryStatus)
        val teamGroup = root.findViewById<RadioGroup>(R.id.teamRadioGroup)
        val badgesGroup = root.findViewById<ChipGroup>(R.id.badgesChipGroup)
        val ownedItemsGroup = root.findViewById<ChipGroup>(R.id.ownedItemsChipGroup)
        val friendsRv = root.findViewById<RecyclerView>(R.id.friendsRecyclerView)
        
        friendAdapter = FriendAdapter { friend ->
            showFriendDetailDialog(friend)
        }
        friendsRv.adapter = friendAdapter
        friendsRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.userName.observe(viewLifecycleOwner) { nameText.text = it }
        viewModel.birthday.observe(viewLifecycleOwner) { birthdayText.text = "Birthday: $it" }
        viewModel.currency.observe(viewLifecycleOwner) { currencyText.text = "Gold: $it" }

        viewModel.socialBattery.observe(viewLifecycleOwner) { progress ->
            if (seekBar.progress != progress) {
                seekBar.progress = progress
            }
            updateStatusText(statusText, progress)
        }

        viewModel.socialXP.observe(viewLifecycleOwner) { xp ->
            xpText.text = getString(R.string.social_xp_label, xp)
        }

        viewModel.selectedTeam.observe(viewLifecycleOwner) { team ->
            when (team) {
                "Introverts" -> root.findViewById<RadioButton>(R.id.radioIntroverts).isChecked = true
                "Extroverts" -> root.findViewById<RadioButton>(R.id.radioExtroverts).isChecked = true
                "Ambiverts" -> root.findViewById<RadioButton>(R.id.radioAmbiverts).isChecked = true
            }
        }

        viewModel.interests.observe(viewLifecycleOwner) { interests ->
            // Use dynamic ChipGroup for interests in profile too, for consistency
            val interestsGroup = root.findViewById<ChipGroup>(R.id.interestsChipGroup)
            interestsGroup.removeAllViews()
            interests.forEach { interest ->
                val chip = Chip(requireContext())
                chip.text = interest
                chip.isCheckable = true
                chip.isChecked = true
                chip.setOnClickListener { viewModel.toggleInterest(interest) }
                interestsGroup.addView(chip)
            }
        }

        viewModel.badges.observe(viewLifecycleOwner) { badges ->
            badgesGroup.removeAllViews()
            badges.forEach { badgeName ->
                val chip = Chip(requireContext())
                chip.text = badgeName
                badgesGroup.addView(chip)
            }
        }

        viewModel.ownedItems.observe(viewLifecycleOwner) { items ->
            ownedItemsGroup.removeAllViews()
            items.forEach { itemName ->
                val chip = Chip(requireContext())
                chip.text = itemName
                ownedItemsGroup.addView(chip)
            }
        }

        viewModel.friends.observe(viewLifecycleOwner) { friends ->
            friendAdapter.submitList(friends)
        }

        setupInterestSearch(root)
        
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.setSocialBattery(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        teamGroup.setOnCheckedChangeListener { _, checkedId ->
            val team = when (checkedId) {
                R.id.radioIntroverts -> "Introverts"
                R.id.radioExtroverts -> "Extroverts"
                R.id.radioAmbiverts -> "Ambiverts"
                else -> "None"
            }
            viewModel.setTeam(team)
        }

        return root
    }

    private fun setupInterestSearch(root: View) {
        val interestSearch = root.findViewById<AutoCompleteTextView>(R.id.profileInterestSearchAutoComplete)
        val allInterests = viewModel.allAvailableInterests
        val searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, allInterests)
        interestSearch.setAdapter(searchAdapter)
        interestSearch.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            viewModel.toggleInterest(selected)
            interestSearch.setText("")
        }
    }

    private fun showFriendDetailDialog(friend: Friend) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_friend_detail, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.dialogFriendName).text = friend.name
        dialogView.findViewById<TextView>(R.id.dialogFriendBirthday).text = "Birthday: ${friend.birthday}"
        dialogView.findViewById<TextView>(R.id.dialogFriendLevel).text = friend.level.toString()
        dialogView.findViewById<TextView>(R.id.dialogFriendBadges).text = friend.badgeCount.toString()
        dialogView.findViewById<TextView>(R.id.dialogFriendScore).text = friend.virtualScore.toString()

        val itemsGroup = dialogView.findViewById<ChipGroup>(R.id.dialogFriendItems)
        itemsGroup.removeAllViews()
        friend.ownedItems.forEach { itemName ->
            val chip = Chip(requireContext())
            chip.text = itemName
            itemsGroup.addView(chip)
        }

        dialogView.findViewById<View>(R.id.btnCloseFriendDialog).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateStatusText(statusText: TextView, progress: Int) {
        val status = when {
            progress < 30 -> getString(R.string.low_energy_status)
            progress < 70 -> getString(R.string.balanced_energy_status)
            else -> getString(R.string.high_energy_status)
        }
        statusText.text = "$status ($progress%)"
    }
}