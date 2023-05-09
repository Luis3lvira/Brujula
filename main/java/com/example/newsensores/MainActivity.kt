package com.example.newsensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var brujula : Sensor
    private lateinit var acelerometro :Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var listener: SensorEventListener

    private var ultimoGrado = 0f
    private var vlrsBrujula = FloatArray(3)
    private var vlsGRavedad = FloatArray(3)
    private var angulosDeOrientacion = FloatArray(3)
    private var matrizDeRotacion = FloatArray(9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bruja : ImageView = findViewById(R.id.bruja)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensores: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensores.forEach { sensor ->
            Log.i("SENSORES", sensor.toString())

        }
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        brujula = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if(brujula != null) {
            Log.i("SENSORES", "El dispositivo tiene brujula")
        }else{
            Log.i("SENDORES", "El dispositivo no tiene brujula")
        }
        //Sensorevelistener
        listener =object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?){

                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER ->{
                        vlsGRavedad = event.values.clone()

                        var x = event.values[0]
                        var y = event.values[1]
                        var z = event.values[2]

                        //Log.i("SENSORES", "Sensor.TYPE_ACCELLEROMETER -> x = $x, y = $y, z = $z")
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        vlrsBrujula = event.values.clone()
                       // Log.i("SENSORES", "Sensor.TYPE_MAGNETIC_FIELD")
                    }
                }
                SensorManager.getRotationMatrix(matrizDeRotacion, null, vlsGRavedad, vlrsBrujula)
                SensorManager.getOrientation(matrizDeRotacion, angulosDeOrientacion)

                val  radian : Float = angulosDeOrientacion[0]
                val gradoActual = (Math.toDegrees(radian.toDouble()) + 360).toFloat() % 360

                var  rotar = RotateAnimation(
                    ultimoGrado, -gradoActual,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotar.duration = 250
                rotar.fillAfter = true

                bruja.startAnimation(rotar)
                ultimoGrado = -gradoActual
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

        }
    }

    override fun onResume(){
        super.onResume()
        sensorManager.registerListener(listener,acelerometro,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener,brujula,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }
}
