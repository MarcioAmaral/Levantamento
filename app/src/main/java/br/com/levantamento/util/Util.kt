package br.com.levantamento.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

class Util {
    companion object {
        fun showSnackBar(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }
}