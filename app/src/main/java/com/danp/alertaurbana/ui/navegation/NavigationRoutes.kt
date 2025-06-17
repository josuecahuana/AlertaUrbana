package com.danp.alertaurbana.ui.navegation

object NavigationRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REPORT = "report"
    const val LIST = "report_list"  // 👈 esta es ahora tu pantalla principal
    const val DETAIL = "report_detail/{reportId}"
    const val USER = "user"

    //Fabian:
    const val MAP = "map"
    const val PROFILE = "profile"
    const val CREATE_REPORT = "create_report"  // Nueva ruta para crear reportes

    fun detailWithId(reportId: Int): String = "report_detail/$reportId"
}