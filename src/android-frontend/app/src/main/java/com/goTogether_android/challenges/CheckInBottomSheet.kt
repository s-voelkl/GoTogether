package com.goTogether_android.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goTogether_android.R
import com.goTogether_android.data.findChallengeByCode

class CheckInBottomSheet : BottomSheetDialogFragment() {

    private lateinit var scanArea: View
    private lateinit var manualArea: View
    private lateinit var successArea: View
    private lateinit var subtitle: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottomsheet_checkin, container, false)

        scanArea = v.findViewById(R.id.scanArea)
        manualArea = v.findViewById(R.id.manualArea)
        successArea = v.findViewById(R.id.successArea)
        subtitle = v.findViewById(R.id.checkInSubtitle)

        v.findViewById<Button>(R.id.enterCodeBtn).setOnClickListener {
            switchToManual()
        }

        v.findViewById<Button>(R.id.backToScanBtn).setOnClickListener {
            switchToScan()
        }

        val codeInput = v.findViewById<EditText>(R.id.codeInput)
        v.findViewById<Button>(R.id.submitCodeBtn).setOnClickListener {
            val code = codeInput.text.toString()
            val challenge = findChallengeByCode(code)
            if (challenge != null) {
                v.findViewById<TextView>(R.id.matchedChallengeName).text = challenge.name
                v.findViewById<TextView>(R.id.matchedChallengePoints).text = "+${challenge.points} pts"
                switchToSuccess()
            } else {
                codeInput.setError("Invalid code")
            }
        }

        v.findViewById<Button>(R.id.doneBtn).setOnClickListener {
            dismiss()
        }

        return v
    }

    private fun switchToManual() {
        scanArea.visibility = View.GONE
        manualArea.visibility = View.VISIBLE
        successArea.visibility = View.GONE
        subtitle.text = "Enter the 5-character code\nshown at the challenge venue"
    }

    private fun switchToScan() {
        scanArea.visibility = View.VISIBLE
        manualArea.visibility = View.GONE
        successArea.visibility = View.GONE
        subtitle.text = "Point your camera at the QR code\nshown at the challenge venue"
    }

    private fun switchToSuccess() {
        scanArea.visibility = View.GONE
        manualArea.visibility = View.GONE
        successArea.visibility = View.VISIBLE
        subtitle.visibility = View.GONE
    }
}
