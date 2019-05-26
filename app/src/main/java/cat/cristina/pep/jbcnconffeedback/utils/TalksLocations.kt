package cat.cristina.pep.jbcnconffeedback.utils


/*
* Important: this room name MUST be the same name as the room name configured in settings
* for each tablet.
*
* */

enum class TalksLocations {

    MON_TC1 { override fun getRoomName() = "Aba English Room" },
    MON_TC2 { override fun getRoomName() = "Apollo Room" },
    MON_TC3 { override fun getRoomName() = "Mariner Room" },
    MON_TC4 { override fun getRoomName() = "Exomars Room" },
    MON_TC5 { override fun getRoomName() = "Voyager Room" },
    TUE_TC1 { override fun getRoomName() = "Aba English Room" },
    TUE_TC2 { override fun getRoomName() = "Apollo Room" },
    TUE_TC3 { override fun getRoomName() = "Mariner Room" },
    TUE_TC4 { override fun getRoomName() = "Exomars Room" },
    TUE_TC5 { override fun getRoomName() = "Voyager Room" },
    WED_TC1 { override fun getRoomName() = "Aba English Room" },
    WED_TC2 { override fun getRoomName() = "Apollo Room" },
    WED_TC3 { override fun getRoomName() = "Mariner Room" },
    WED_TC4 { override fun getRoomName() = "Exomars Room" },
    WED_TC5 { override fun getRoomName() = "Voyager Room" };

    abstract fun getRoomName(): String
}