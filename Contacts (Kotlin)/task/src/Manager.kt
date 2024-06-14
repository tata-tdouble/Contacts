package contacts

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.File
import java.time.LocalDate


class Manager {

    private val contactStore = mutableListOf<Entity>()

    fun start() {
        var isAlive = true
        while (isAlive) {
            loadData(PATH)
            isAlive = displayMenu()
            saveData(PATH)
        }
    }

    fun displayMenu(): Boolean {
        val input = readInput(MENU)
        return when(input){
            INPUT_ADD -> addContact()
            INPUT_LIST -> listContact()
            INPUT_SEARCH -> searchContact()
            INPUT_COUNT -> countContact()
            else -> false
        }
    }

    fun addContact(): Boolean {
        val input = readInput(ENTER_THE_TYPE)
        return when(input){
            PERSON -> addContactPerson()
            ORGANIZATION -> addContactOrganization()
            else -> false
        }
    }

    fun addContactPerson(): Boolean {
        contactStore.add(personBuilder {
            properties[PROPERTY_NAME] = readInput(ENTER_THE_NAME)
            properties[PROPERTY_SURNAME] = readInput(ENTER_THE_SURNAME)
            properties[PROPERTY_BIRTH_DATE] = checkBirthdate(readInput(ENTER_THE_BIRTH_DATE))
            properties[PROPERTY_GENDER] = checkGender(readInput(ENTER_THE_GENDER))
            properties[PROPERTY_NUMBER] = checkNumber(readInput(ENTER_THE_PHONE_NUMBER))
            properties[PROPERTY_TIME_CREATED] = getCurrentDate()
            properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
        })
        writeOutput(RESPONSE_RECORD_ADDED)
        println()
        return true
    }

    fun checkNumber(string: String): String {
        return if (isValidNumber(string)) string else "[no number]"
    }

    fun checkBirthdate(string: String): String {
        try {
            val localDate = LocalDate.parse(string)
            return localDate.toString()
        } catch (e : Exception){
            println("Bad birth date!")
            return "[no data]"
        }
    }

    fun checkGender(string: String): String {
        val regex = Regex("[MF]")
        return if (regex.matches(string)){
            string
        } else{
            println("Bad gender!")
            "[no data]"
        }
    }

    fun addContactOrganization(): Boolean {
        contactStore.add(organizationBuilder {
            properties[PROPERTY_ORG_NAME] = readInput(ENTER_THE_ORGANIZATION_NAME)
            properties[PROPERTY_ORG_ADDRESS] = readInput(ENTER_THE_ADDRESS)
            properties[PROPERTY_ORG_NUMBER] = readInput(ENTER_THE_PHONE_NUMBER)
            properties[PROPERTY_TIME_CREATED] = getCurrentDate()
            properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
        })
        writeOutput(RESPONSE_RECORD_ADDED)
        println()
        return true
    }

    fun searchContact(): Boolean {
        val query = readInput(ENTER_SEARCH_QUERY)
        val res = performSearch(query)
        println("Found ${res.size} results:")
        logContacts(res)
        println()
        return searchAction(res)
    }

    fun searchAction(results: MutableList<Entity>) : Boolean {
        println()
        val input = readInput(MENU_ENTER_SEARCH_ACTION)
        return when (input) {
            INPUT_BACK -> true
            INPUT_AGAIN -> searchContact()
            else -> {
                if (input.trim().toInt() in (1..results.size)){
                    val mIndex = input.trim().toInt() - 1
                    contactStore[mIndex].display()
                    println()
                    recordAction(mIndex)
                } else {
                    writeOutput(RESPONSE_WRONG_INPUT)
                    true
                }
            }
        }
    }

    fun performSearch(query: String): MutableList<Entity> {
        val list = mutableListOf<Entity>()
        for(i in contactStore){
            val data = buildString {
                for(j in i.properties){
                    append(j.value)
                }
            }
            if ((data.lowercase()).contains(Regex(query.lowercase())))
                list.add(i)
        }
        return list
    }

    fun recordAction(index : Int) : Boolean{
        val input = readInput(MENU_RECORD_ENTER_ACTION)
        when (input) {
            INPUT_EDIT -> editContact(index)
            INPUT_DELETE -> removeContact(index)
            INPUT_MENU -> { println()
                return true
            }
            else -> {
                writeOutput(RESPONSE_WRONG_INPUT)
                return true
            }
        }
        println()
        return recordAction(index)
    }

    fun removeContact(index: Int): Boolean {
        if (contactStore.size == 0){
            writeOutput(RESPONSE_NO_RECORDS_TO_REMOVE)
            return true
        }
        if (index in contactStore.indices.map { (it + 1) } ) {
            contactStore.removeAt(index)
            writeOutput(RESPONSE_RECORD_REMOVED)
        } else {
            writeOutput(RESPONSE_WRONG_INPUT)
        }
        return true
    }

    fun editContact(index: Int): Boolean {
        if (contactStore.size == 0){
            writeOutput(RESPONSE_NO_RECORDS_TO_EDIT)
            return true
        }
        if (index in contactStore.indices ) {
            val contact = contactStore[index].properties[PROPERTY_IS_PERSON]!!
            if (contact.equals(TRUE))
                editContactPerson(index)
            else
                editContactOrganization(index)
        } else {
            writeOutput(RESPONSE_WRONG_INPUT)
        }
        return true
    }

    fun editContactPerson(index: Int): Boolean {
        val text = readInput(SELECT_A_FIELD_PERSON)
        val contact = contactStore[index]
        when (text){
            NAME -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_NAME] = readInput(ENTER_NAME)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            SURNAME -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_SURNAME] = readInput(ENTER_SURNAME)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            BIRTHDATE -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_BIRTH_DATE] = readInput(ENTER_BIRTH)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            GENDER -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_GENDER] = readInput(ENTER_GENDER)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            PHONE_NUMBER -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_NUMBER] = readInput(ENTER_NUMBER)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            else -> writeOutput(RESPONSE_WRONG_INPUT)
        }
        writeOutput(RESPONSE_RECORD_UPDATED)
        println()
        return true
    }

    fun editContactOrganization(index: Int): Boolean {
        val text = readInput(SELECT_A_FIELD_ORGANIZATION)
        val contact = contactStore[index] as Organization
        when (text){
            ADDRESS -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_ORG_ADDRESS] = readInput(ENTER_ADDRESS)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            PHONE_NUMBER -> {
                contactStore[index] = contact.apply {
                    properties[PROPERTY_ORG_NUMBER] = readInput(ENTER_NUMBER)
                    properties[PROPERTY_TIME_LAST_EDIT] = getCurrentDate()
                }
            }
            else -> writeOutput(RESPONSE_WRONG_INPUT)
        }
        writeOutput(RESPONSE_RECORD_UPDATED)
        println()
        return true
    }

    fun listContact(): Boolean {
        if (contactStore.size == 0){
            writeOutput(RESPONSE_NO_RECORDS_TO_DISPLAY)
            return true
        }
        logContacts(contactStore)
        println()
        val input = readInput(MENU_LIST_ENTER_SEARCH)
        return when {
            input in contactStore.indices.map { (it + 1).toString() } -> {
                val index = input.toInt() - 1
                contactStore[index].display()
                println()
                recordAction(index)
            }
            else -> true
        }
    }

    fun countContact(): Boolean {
        println("The Phone Book has ${contactStore.size} records.")
        println()
        return true
    }

    fun getCurrentDate() = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(LocalDateTime.now())

    fun readInput(string: String): String {
        print(string)
        return readln().trim()
    }

    fun writeOutput(string: String) {
        println(string)
    }

    fun logContacts(value: MutableList<Entity>) {
        for (i in value.indices){
            val contact = value[i]
            if (contact.properties[PROPERTY_IS_PERSON].equals(TRUE))
                println("${i + 1}. ${contact.properties[PROPERTY_NAME]} ${contact.properties[PROPERTY_SURNAME]}")
             else
                println("${i + 1}. ${contact.properties[PROPERTY_ORG_NAME]}")
        }
    }

    fun isValidNumber(value: String): Boolean {
        val regex = Regex("""^\+?((\(?\w+\)?[-\s]?)|((\(\w+\)[-\s])(\w{2,}[-\s]?))|((\w+[-\s]?)(\(\w{2,}\)[-\s]?))|((\w+)(\w{2,}[-\s]?)))(\w{2,}[-\s]?)*""")
        return regex.matches(value)
    }

    fun saveData(filePath: String) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(MutableList::class.java, Entity::class.java)
        val adapter =  moshi.adapter<MutableList<Entity>>(type)
        try {
            val file = File(filePath)
            file.writeText(adapter.toJson(contactStore))
        } catch (e: Exception) {
            // Handle exception (e.g., file not found, permission issues)
            println("Error saving data: $e")
        }
    }

    fun loadData(filePath: String): MutableList<Entity> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(MutableList::class.java, Entity::class.java)
        val adapter =  moshi.adapter<MutableList<Entity>>(type)
        try {
            val file = File(filePath)
            val jsonData = file.readText()
            return adapter.fromJson(jsonData) ?: mutableListOf()
        } catch (e: Exception) {
            val file = File(filePath)
            file.createNewFile()
            return mutableListOf()
        }
    }

    companion object {

        const val MENU = "[menu] Enter action (add, list, search, count, exit): "
        const val PATH = "contacts.json"

        const val MENU_ENTER_SEARCH_ACTION = "[search] Enter action ([number], back, again): "
        const val MENU_RECORD_ENTER_ACTION = "[record] Enter action (edit, delete, menu): "
        const val MENU_LIST_ENTER_SEARCH = "[list] Enter action ([number], back): "

        const val ENTER_SEARCH_QUERY = "Enter search query: "

        const val ENTER_THE_NAME = "Enter the name: "
        const val ENTER_THE_SURNAME = "Enter the surname: "
        const val ENTER_THE_PHONE_NUMBER = "Enter the number: "
        const val ENTER_THE_TYPE = "Enter the type (person, organization): "
        const val ENTER_THE_BIRTH_DATE = "Enter the birth date: "
        const val ENTER_THE_GENDER = "Enter the gender (M, F): "
        const val ENTER_THE_ORGANIZATION_NAME = "Enter the organization name: "
        const val ENTER_THE_ADDRESS = "Enter the address: "

        const val ENTER_NAME = "Enter name: "
        const val ENTER_SURNAME = "Enter surname: "
        const val ENTER_NUMBER = "Enter number: "
        const val ENTER_BIRTH = "Enter birth: "
        const val ENTER_GENDER = "Enter gender: "
        const val ENTER_ADDRESS = "Enter address: "

        const val SELECT_A_FIELD_PERSON = "Select a field (name, surname, birth, gender, number): "
        const val SELECT_A_FIELD_ORGANIZATION = "Select a field (address, number): "

        const val RESPONSE_NO_RECORDS_TO_EDIT = "No records to edit!"
        const val RESPONSE_NO_RECORDS_TO_REMOVE = "No records to remove!"
        const val RESPONSE_NO_RECORDS_TO_DISPLAY = "No records to display!"
        const val RESPONSE_RECORD_ADDED = "The record added."

        const val RESPONSE_RECORD_UPDATED = "The record updated!"

        const val RESPONSE_RECORD_REMOVED = "The record removed!"
        const val RESPONSE_WRONG_INPUT = "Wrong input!"

        const val INPUT_ADD = "add"
        const val INPUT_DELETE = "delete"
        const val INPUT_LIST = "list"
        const val INPUT_SEARCH = "search"
        const val INPUT_EDIT = "edit"
        const val INPUT_COUNT = "count"
        const val INPUT_BACK = "back"
        const val INPUT_AGAIN = "again"
        const val INPUT_MENU = "menu"

        const val PERSON = "person"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val BIRTHDATE= "birth"
        const val GENDER = "gender"
        const val PHONE_NUMBER = "number"
        const val TRUE = "true"
        const val ORGANIZATION = "organization"
        const val ADDRESS = "address"

        const val PROPERTY_NAME = "_name"
        const val PROPERTY_SURNAME = "_surname"
        const val PROPERTY_BIRTH_DATE= "_birthdate"
        const val PROPERTY_GENDER = "_gender"
        const val PROPERTY_NUMBER = "_phone_number"
        const val PROPERTY_TIME_CREATED = "_time_created"
        const val PROPERTY_TIME_LAST_EDIT = "_time_edit"
        const val PROPERTY_IS_PERSON = "_isPerson"

        const val PROPERTY_ORG_NAME = "_name"
        const val PROPERTY_ORG_ADDRESS = "surname"
        const val PROPERTY_ORG_NUMBER = "phone_number"

    }
}

