package contacts

abstract class Contact {

    abstract var properties : MutableMap<String, String>

    abstract fun getAvailableProperties(): List<String>

    abstract fun setProperty(propertyName: String, newValue: Any?)

    abstract fun getProperty(propertyName: String): Any?

    abstract fun display()
}


open class Entity : Contact() {

    override var properties = mutableMapOf<String, String>()

    fun hasNumber(): Boolean {
        return properties.get("phone_number").toString().isNotEmpty()
    }

    override fun getAvailableProperties(): List<String> {
        return properties.keys.toList()
    }

    override fun setProperty(propertyName: String, newValue: Any?) {
        properties[propertyName] = newValue.toString()
    }

    override fun getProperty(propertyName: String): Any? {
        return properties[propertyName]
    }

    override fun display() {

    }


}

class Person : Entity() {

    override var properties = mutableMapOf<String, String>()

    init {
        properties.run {
            put(PROPERTY_NAME, "")
            put(PROPERTY_SURNAME, "")
            put(PROPERTY_BIRTH_DATE, "")
            put(PROPERTY_GENDER, "")
            put(PROPERTY_NUMBER, "")
            put(PROPERTY_TIME_CREATED, "")
            put(PROPERTY_TIME_LAST_EDIT, "")
            put(PROPERTY_IS_PERSON, "true")
        }
    }


    override fun display() {
        println("$INFO_NAME${properties[PROPERTY_NAME]}")
        println("$INFO_SURNAME${properties[PROPERTY_SURNAME]}")
        println("$INFO_BIRTH_DATE${properties[PROPERTY_BIRTH_DATE]}")
        println("$INFO_GENDER${properties[PROPERTY_GENDER]}")
        println("$INFO_NUMBER${properties[PROPERTY_NUMBER]}")
        println("$INFO_TIME_CREATED${properties[PROPERTY_TIME_CREATED]}")
        println("$INFO_TIME_LAST_EDIT${properties[PROPERTY_TIME_LAST_EDIT]}")
    }

    companion object {

        const val INFO_NAME = "Name: "
        const val INFO_SURNAME = "Surname: "
        const val INFO_BIRTH_DATE = "Birth date: "
        const val INFO_GENDER = "Gender: "
        const val INFO_NUMBER = "Number: "
        const val INFO_TIME_CREATED = "Time created: "
        const val INFO_TIME_LAST_EDIT = "Time last edit: "

        const val PROPERTY_NAME = "_name"
        const val PROPERTY_SURNAME = "_surname"
        const val PROPERTY_BIRTH_DATE= "_birthdate"
        const val PROPERTY_GENDER = "_gender"
        const val PROPERTY_NUMBER = "_phone_number"
        const val PROPERTY_TIME_CREATED = "_time_created"
        const val PROPERTY_TIME_LAST_EDIT = "_time_edit"
        const val PROPERTY_IS_PERSON = "_isPerson"

    }

}

class Organization : Entity() {

    override var properties = mutableMapOf<String, String>()

    init {
        properties.run {
            put(PROPERTY_ORG_NAME, "")
            put(PROPERTY_ORG_ADDRESS, "")
            put(PROPERTY_ORG_NUMBER, "")
            put(PROPERTY_TIME_CREATED, "")
            put(PROPERTY_TIME_LAST_EDIT, "")
            put(PROPERTY_IS_PERSON, "false")
        }
    }

    override fun display() {
        println("$INFO_ORG_NAME${properties[PROPERTY_ORG_NAME]}")
        println("$INFO_ORG_ADDRESS${properties[PROPERTY_ORG_ADDRESS]}")
        println("$INFO_NUMBER${properties[PROPERTY_ORG_NUMBER]}")
        println("$INFO_TIME_CREATED${properties[PROPERTY_TIME_CREATED]}")
        println("$INFO_TIME_LAST_EDIT${properties[PROPERTY_TIME_LAST_EDIT]}")
    }

    companion object {

        const val INFO_ORG_NAME = "Organization name: "
        const val INFO_ORG_ADDRESS = "Address: "
        const val INFO_NUMBER = "Number: "
        const val INFO_TIME_CREATED = "Time created: "
        const val INFO_TIME_LAST_EDIT = "Time last edit: "

        const val PROPERTY_ORG_NAME = "_name"
        const val PROPERTY_ORG_ADDRESS = "surname"
        const val PROPERTY_ORG_NUMBER = "phone_number"
        const val PROPERTY_TIME_CREATED = "_time_created"
        const val PROPERTY_TIME_LAST_EDIT = "_time_edit"
        const val PROPERTY_IS_PERSON = "_isPerson"


    }
}

fun personBuilder(init: Person.() -> Unit): Person {
    val person = Person()
    person.init()
    return person
}

fun organizationBuilder(init: Organization.() -> Unit): Organization {
    val organization = Organization()
    organization.init()
    return organization
}
