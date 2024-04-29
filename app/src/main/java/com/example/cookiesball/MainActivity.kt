package com.example.cookiesball

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookiesball.databinding.ActivityMainBinding
import com.example.cookiesball.databinding.ActivityStartBinding
import java.io.BufferedReader
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val results = mutableListOf<String>()
    var goalNumString: String = generateGoalNum()
    var inputNumString = ""
    val matchResultMap = mutableMapOf("스트라이크" to 0, "볼" to 0, "미스" to 0)
    var flag = "0" // 0 : 게임 중, 1 : goalNumber 생성 필요, 2 : 게임 종료
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom + 20)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resultRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.resultRecyclerview.adapter = MyAdapter(results)
        binding.resultRecyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.systemText.text = "숫자를 입력해 주세요"

        binding.inputBtn.setOnClickListener {
            var newResult: String? = null
            if (!gameOver) {
                newResult = play(binding.inputText.text.toString())
                results.add(newResult!!)
            } else {
                flag = binding.inputText.text.toString()
                if (!checkValidFlag(flag)) {
                    binding.systemText.text = "1 또는 2를 입력해주세요\n"
                } else {
                    gameOver = false
                    results.clear()
                    if (flag == "1") {
                        binding.systemText.text = "숫자를 입력해 주세요"
                    } else {
                        val intent = Intent(this, StartActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            binding.inputText.text.clear()

            (binding.resultRecyclerview.adapter as MyAdapter).notifyDataSetChanged()
            binding.inputText.text.clear()
        }
    }

    private fun play(inputNumString: String): String {
        resetMatchResultMap(matchResultMap)

        if (flag == "1" || flag == "2") {
            goalNumString = generateGoalNum()
            flag = "0"
            gameOver = false
        }

        binding.systemText.text = "숫자를 입력해 주세요"

        if (!checkInputNum(inputNumString)) {
            binding.systemText.text = "1부터 9까지 서로 다른 수로 이루어진 3자리의 수를 입력해주세요."
        }

        matchGoalNumAndUserNum(goalNumString, inputNumString, matchResultMap)

        if (matchResultMap["스트라이크"] == 3) {
            printGameOverMessage()
            gameOver = true
        }

        return inputNumString + ": " + matchResultMap.toString()
    }


    fun generateGoalNum(): String {
        var goalNumString: String = Random.nextInt(123, 988).toString()
        while (!isCorrectGoalNum(goalNumString)) {
            goalNumString = Random.nextInt(123, 988).toString()
        }
        return goalNumString
    }

    fun isCorrectGoalNum(goalNumString: String): Boolean {
        if (isNotValidNumber(goalNumString)) {
            return false
        }
        if (isNotDistinctNumber(goalNumString)) {
            return false
        }
        return true
    }


    fun checkInputNum(inputNumString: String): Boolean {
        if (isNotValidNumber(inputNumString)) {
            return false
        } else if (isNotThreeDigitNumber(inputNumString)) {
            return false
        } else if (isNotDistinctNumber(inputNumString)) {
            return false
        }
        return true
    }

    fun isNotValidNumber(numString: String): Boolean {
        numString.forEach { n ->
            if (n !in '1'..'9') {
                return true
            }
        }
        return false
    }

    fun isNotThreeDigitNumber(inputNumString: String): Boolean {
        val inputNum: Int = inputNumString.toInt()
        return inputNum !in 100..999
    }

    fun isNotDistinctNumber(numString: String): Boolean {
        return (numString[0] == numString[1] || numString[0] == numString[2] || numString[1] == numString[2])
    }

    fun matchGoalNumAndUserNum(
        goalNumString: String,
        inputNumString: String,
        matchResultMap: MutableMap<String, Int>
    ) {
        for (i in 0..2) {
            if (isStrike(i, inputNumString[i], goalNumString)) {
                matchResultMap["스트라이크"] = matchResultMap["스트라이크"]!! + 1
            } else if (isBall(i, inputNumString[i], goalNumString)) {
                matchResultMap["볼"] = matchResultMap["볼"]!! + 1
            } else {
                matchResultMap["미스"] = matchResultMap["미스"]!! + 1
            }
        }
    }

    fun isStrike(index: Int, curNumChar: Char, goalNumString: String): Boolean {
        goalNumString.forEachIndexed { i, n ->
            if (i == index && n == curNumChar) {
                return true
            }
        }
        return false
    }

    fun isBall(index: Int, curNumChar: Char, goalNumString: String): Boolean {
        goalNumString.forEachIndexed { i, n ->
            if (i != index && n == curNumChar) {
                return true
            }
        }
        return false
    }


    fun printGameOverMessage() {
        binding.systemText.text = "3개의 숫자를 모두 맞히셨습니다! 게임 종료\n게임을 새로 시작하려면 1, 종료하려면 2를 입력하세요."
    }

    fun checkValidFlag(flag: String): Boolean {
        if (flag != "1" && flag != "2") {
            return false
        }
         return true
    }


    fun resetMatchResultMap(matchResultMap: MutableMap<String, Int>) {
        matchResultMap["스트라이크"] = 0
        matchResultMap["볼"] = 0
        matchResultMap["미스"] = 0
    }


}