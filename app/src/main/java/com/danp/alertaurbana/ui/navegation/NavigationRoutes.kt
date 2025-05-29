package com.danp.alertaurbana.ui.navegation

object NavigationRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val REPORT = "report"
    const val LIST = "report_list"  // 👈 esta es ahora tu pantalla principal
    const val DETAIL = "report_detail/{reportId}"
    const val USER = "user"

    fun detailWithId(reportId: Int): String = "report_detail/$reportId"
}