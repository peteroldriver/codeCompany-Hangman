package com.example.codecompany_hangman

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var lettersUsed: String = ""
    private lateinit var question: String
    private lateinit var questionhint: String
    private lateinit var curquestion: String
    private lateinit var word: TextView
    private lateinit var hint: TextView
    private var hintCnt: Int = 0
    private var hangingTime: Int = 0
    val Buttons = listOf(
        R.id.a, R.id.b, R.id.c, R.id.d, R.id.e,
        R.id.f, R.id.g, R.id.h, R.id.i, R.id.j,
        R.id.k, R.id.l, R.id.m, R.id.n, R.id.o,
        R.id.p, R.id.q, R.id.r, R.id.s, R.id.t,
        R.id.u, R.id.v, R.id.w, R.id.x, R.id.y,
        R.id.z, R.id.newgame, R.id.hintbutton
    )
    val diableButtons = listOf(
        R.id.a, R.id.b, R.id.c, R.id.d, R.id.e,
        R.id.f, R.id.g, R.id.h, R.id.i, R.id.j,
        R.id.k, R.id.l, R.id.m, R.id.n, R.id.o,
        R.id.p, R.id.q, R.id.r, R.id.s, R.id.t,
        R.id.u, R.id.v, R.id.w, R.id.x, R.id.y,
        R.id.z, R.id.hintbutton
    )

    private lateinit var gameImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pair= getQuestion()
        question = pair.first
        questionhint = pair.second
        curquestion = getCurrentQ(question, "")


        // List of all button IDs

        // Setting the same click listener for all buttons
        Buttons.forEach { button ->
            findViewById<Button>(button).setOnClickListener { view ->
                // Handle the button click
                onButtonClick(view)
            }
        }

        updateWord()

    }
    fun newGame() {
        val pair= getQuestion()
        question = pair.first
        questionhint = pair.second
        curquestion = getCurrentQ(question, "")
        lettersUsed = ""
        hintCnt = 0
        hangingTime = 0
        updateWord()
        updateHint("")

        // Enable all buttons
        for (button in diableButtons) {
            findViewById<Button>(button).isEnabled = true
        }
        // Reset the image
        gameImage = findViewById(R.id.gameimage)
        gameImage.setImageResource(R.drawable.game0)
    }

    fun getQuestion():Pair<String, String>{
        val random = (0 until Questions.words.size).random()
        val question = Questions.words[random]
        val questionhint = Questions.hints[random]
        return Pair(question, questionhint)
    }

    fun getCurrentQ(curQuestion: String, usedLetter: String): String {
        var display = ""
        for (letter in curQuestion) {
            if (usedLetter.contains(letter)) {
                display += letter
            } else {
                display += "_"
            }
        }
        //if display not contain "_", then game won
        if (!display.contains("_")) {
            GameWon()
        }
        return display

    }

    fun onButtonClick(view: View) {
        Log.i("GameActivity", "Button clicked: ${view.id}")
        // You can use the view.id to determine which button was clicked
        when (view.id) {
            R.id.newgame -> {
//                Toast.makeText(
//                    this,
//                    question + " " + curquestion + " " + lettersUsed,
//                    Toast.LENGTH_SHORT
//                ).show()
                newGame()
            }

            R.id.hintbutton -> {
                getHint(hintCnt)
                hintCnt++
            }

            else -> {
                // Handle the letter button click
                disableButton(view)
                checkword(view)
            }
        }
    }

    fun disableButton(view: View) {
        // Disable the button
        view.isEnabled = false
        // Add the letter to the lettersUsed string
        lettersUsed += (view as Button).text.toString().lowercase()
    }

    fun checkword(view: View) {
//        Toast.makeText(this, "Letter: " + (view as Button).text, Toast.LENGTH_SHORT).show()
        val letter = (view as Button).text
        if (!question.contains(letter, ignoreCase = true)) {
            hang()
        }
        curquestion = getCurrentQ(question, lettersUsed)
//        Toast.makeText(
//            this,
//            hangingTime.toString() + " " + question + " " + lettersUsed + " " + getCurrentQ(
//                question,
//                lettersUsed
//            ),
//            Toast.LENGTH_SHORT
//        ).show()
        updateWord()
    }

    fun updateWord() {
        // Update the word TextView
        word = findViewById(R.id.word)
        word.text = curquestion
    }

    fun updateHint(inputText: String) {
        hint = findViewById(R.id.hinttext)
        hint.text = inputText
    }

    fun getHint(hintCnt: Int) {
        // switch case
        when (hintCnt) {
            0 -> {
                updateHint(questionhint)
            }

            1 -> {
                var remaining = (26 - lettersUsed.length) / 2
                // through the remaining letters, find the first letter that is not used
                val letterButton = listOf(
                    R.id.a, R.id.b, R.id.c, R.id.d, R.id.e,
                    R.id.f, R.id.g, R.id.h, R.id.i, R.id.j,
                    R.id.k, R.id.l, R.id.m, R.id.n, R.id.o,
                    R.id.p, R.id.q, R.id.r, R.id.s, R.id.t,
                    R.id.u, R.id.v, R.id.w, R.id.x, R.id.y,
                    R.id.z
                )
                // iterate through button list and disable half of the remaining letters
                for (button in letterButton) {
                    var cur_button = findViewById<Button>(button)
                    if (remaining > 0) {
                        if (cur_button.isEnabled) {
                            disableButton(cur_button)
                            remaining--
                        }
                    }
                }
            }

            2 -> {
                if (checkHangingTime()) {
                    hang()
                    val vowelButton = listOf(
                        R.id.a, R.id.e, R.id.i, R.id.o, R.id.u
                    )
                    for (button in vowelButton) {
                        var cur_button = findViewById<Button>(button)
                        if (cur_button.isEnabled) {
                            disableButton(cur_button)
                        }
                    }
                } else {
                    updateHint("No more hints available")}
            }
        }


    }

    fun checkHangingTime(): Boolean {
        if (hangingTime > 4) {
            return false
        }
        return true
    }

    fun hang() {
        hangingTime++
        // update image
        // Construct the image name dynamically
        var imageName = "game" + hangingTime + ".png"
        val gameImage: ImageView =
            findViewById(R.id.gameimage) // Replace 'yourImageViewId' with the actual ID of your ImageView
        val imageResId =
            resources.getIdentifier(imageName.substringBeforeLast("."), "drawable", packageName)
        if (imageResId != 0) {
            // If the resource was found, set it as the image for the ImageView
            gameImage.setImageResource(imageResId)
        }
        if (hangingTime > 5) {
            GameLost()
        }
    }

    fun GameLost() {
        // Display a message to the user
//        Toast.makeText(this, "You lost!", Toast.LENGTH_SHORT).show()
        // change the word to the correct answer
        curquestion = question
        updateWord()
        updateHint("YOU LOST!")
        // Disable all buttons
        for (button in diableButtons) {
            findViewById<Button>(button).isEnabled = false
        }
    }

    fun GameWon() {
        // Display a message to the user
//        Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show()
        updateHint("YOU WON!")
        // Disable all buttons
        for (button in diableButtons) {
            findViewById<Button>(button).isEnabled = false
        }
    }


}



