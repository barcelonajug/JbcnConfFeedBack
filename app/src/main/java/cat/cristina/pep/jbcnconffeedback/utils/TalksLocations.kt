package cat.cristina.pep.jbcnconffeedback.utils


/*
* Important: this room name MUST be the same name as the room name configured in settings
* for each tablet.
*
* */

enum class TalksLocations {

    MON_TC1 { override fun getRoomName() = "Aba English Room" },
    MON_TC2 { override fun getRoomName() = "Apollo" },
    MON_TC3 { override fun getRoomName() = "Mariner" },
    MON_TC4 { override fun getRoomName() = "Exomars" },
    MON_TC5 { override fun getRoomName() = "Voyager" },
    TUE_TC1 { override fun getRoomName() = "Aba English Room" },
    TUE_TC2 { override fun getRoomName() = "Apollo" },
    TUE_TC3 { override fun getRoomName() = "Mariner" },
    TUE_TC4 { override fun getRoomName() = "Exomars" },
    TUE_TC5 { override fun getRoomName() = "Voyager" },
    WED_TC1 { override fun getRoomName() = "Aba English Room" },
    WED_TC2 { override fun getRoomName() = "Apollo" },
    WED_TC3 { override fun getRoomName() = "Mariner" },
    WED_TC4 { override fun getRoomName() = "Exomars" },
    WED_TC5 { override fun getRoomName() = "Voyager" };

    abstract fun getRoomName(): String
}