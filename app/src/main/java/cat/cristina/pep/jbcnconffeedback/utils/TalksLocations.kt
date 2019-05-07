package cat.cristina.pep.jbcnconffeedback.utils


/*
* Important: this room name MUST be the same name as the room name configured in settings
* for each tablet.
*
* */

enum class TalksLocations {

    MON_TC1 { override fun getRoomName() = "Room 1" },
    MON_TC2 { override fun getRoomName() = "Room 2" },
    MON_TC3 { override fun getRoomName() = "Room 3" },
    MON_TC4 { override fun getRoomName() = "Room 4" },
    MON_TC5 { override fun getRoomName() = "Room 5" },
    TUE_TC1 { override fun getRoomName() = "Room 1" },
    TUE_TC2 { override fun getRoomName() = "Room 2" },
    TUE_TC3 { override fun getRoomName() = "Room 3" },
    TUE_TC4 { override fun getRoomName() = "Room 4" },
    TUE_TC5 { override fun getRoomName() = "Room 5" },
    WED_TC1 { override fun getRoomName() = "Room 1" },
    WED_TC2 { override fun getRoomName() = "Room 2" },
    WED_TC3 { override fun getRoomName() = "Room 3" },
    WED_TC4 { override fun getRoomName() = "Room 4" },
    WED_TC5 { override fun getRoomName() = "Room 5" };

    abstract fun getRoomName(): String
}