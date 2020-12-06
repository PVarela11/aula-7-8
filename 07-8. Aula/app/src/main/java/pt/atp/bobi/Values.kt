package pt.atp.bobi

import android.os.Build.MANUFACTURER
import android.os.Build.MODEL

const val ACTIVITY_REQUEST = "activity.request"
const val EXTRA_USERNAME = "extra.username"

const val INVALID_ID = -1

const val EXTRA_DOG_NAME = "extra.dog.name"
const val EXTRA_DOG_BREED = "extra.dog.breed"

fun deviceName() = "$MODEL-$MANUFACTURER"