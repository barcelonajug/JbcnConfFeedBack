package cat.cristina.pep.jbcnconffeedback.utils


/*
* Important: this room name MUST be the same name as the room name configured in settings
* for each tablet.
*
* */

enum class TalksLocations {

    MON_TC1 { override fun getRoomName() = "Grace Hopper" },
    MON_TC2 { override fun getRoomName() = "Katherine Johnson" },
    MON_TC3 { override fun getRoomName() = "Dynatrace" },
    MON_TC4 { override fun getRoomName() = "Oracle NetSuite" },
    MON_TC5 { override fun getRoomName() = "Ada Lovelace" },
    TUE_TC1 { override fun getRoomName() = "Grace Hopper" },
    TUE_TC2 { override fun getRoomName() = "Katherine Johnson" },
    TUE_TC3 { override fun getRoomName() = "Dynatrace" },
    TUE_TC4 { override fun getRoomName() = "Oracle NetSuite" },
    TUE_TC5 { override fun getRoomName() = "Ada Lovelace" },
    WED_TC1 { override fun getRoomName() = "Margaret Hamilton" },
    WED_TC2 { override fun getRoomName() = "Barbara Liskov" },
    WED_TC3 { override fun getRoomName() = "Dynatrace" },
    WED_TC4 { override fun getRoomName() = "Oracle NetSuite" },
    WED_TC5 { override fun getRoomName() = "Megan Smith" };

    abstract fun getRoomName(): String
}