package fr.stark.steauc.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import fr.stark.steauc.R
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Error
import fr.stark.steauc.log.Message
import fr.stark.steauc.SceneActivity
import fr.stark.steauc.kalman.DataSet

const val NOTIFY_DELAY : Long = 0

class BLEServiceAdapter(
    private val gatt         : BluetoothGatt?,
    private val serviceList  : MutableList<BLEService>,
    private val givenScene : SceneActivity
) : ExpandableRecyclerViewAdapter<
    BLEServiceAdapter.ServiceViewHolder,
    BLEServiceAdapter.CharacteristicViewHolder
>(serviceList) {

    //debug info
    private val info : CodeInfo = CodeInfo("BLE", "ble/BLEServiceAdapter.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //notification subscription
    private var enabled : Boolean = false
    private val scene : SceneActivity = givenScene




    //DATA CLASSES

    //parent
    class ServiceViewHolder(itemView: View) : GroupViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.ble_service_name)
        val serviceUUID: TextView = itemView.findViewById(R.id.ble_service_uuid)
    }

    //child
    class CharacteristicViewHolder(itemView: View) : ChildViewHolder(itemView) {
        val characteristicUUID       : TextView = itemView.findViewById(R.id.ble_service_characteristic_uuid)
        val characteristicProperties : TextView = itemView.findViewById(R.id.ble_service_properties)
        val characteristicValue      : TextView = itemView.findViewById(R.id.ble_service_value)
        val characteristicNom        : TextView = itemView.findViewById(R.id.ble_service_characteristic)

        val characteristicReadAction   : Button = itemView.findViewById(R.id.ble_service_read_button)
        val characteristicWriteAction  : Button = itemView.findViewById(R.id.ble_service_write_button)
        val characteristicNotifyAction : Button = itemView.findViewById(R.id.ble_service_notify_button)

    }




    //VIEW HOLDERS

    //parent
    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder =
        ServiceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ble_service_rec_view_cell,
                parent,
                false
            )
        )

    //child
    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : CharacteristicViewHolder =
        CharacteristicViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ble_service_characteristic_rec_view_cell,
                parent,
                false
            )
        )




    //BIND

    //parent
    override fun onBindGroupViewHolder(
        holder: ServiceViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        msg.function("onBindGroupViewHolder")

        val title = BLEUUIDAttribute.getBLEAttributeFromUUID(group.title).title
        holder.serviceName.text = title
        holder.serviceUUID.text = group.title
    }

    //child
    @SuppressLint("SetTextI18n")
    override fun onBindChildViewHolder(
        holder: CharacteristicViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        msg.function("onBindChildViewHolder")
        val characteristic: BluetoothGattCharacteristic = (group as BLEService).items[childIndex]


        //LAYOUT

        //set basic attributes
        holder.characteristicUUID.text = "UUID : ${characteristic.uuid}"
        holder.characteristicNom.text = BLEUUIDAttribute.getBLEAttributeFromUUID(group.title).title
        val prop = setPropertiesString(characteristic.properties)

        //set read availability
        holder.characteristicReadAction.visibility = View.GONE
        if( prop.contains("Read") ) {
            holder.characteristicReadAction.visibility = View.VISIBLE
        }

        //set write availability
        holder.characteristicWriteAction.visibility = View.GONE
        if( prop.contains("Write") ){
            holder.characteristicWriteAction.visibility = View.VISIBLE
        }

        //set notification availability
        holder.characteristicNotifyAction.visibility = View.GONE
        if( prop.contains("Notify") ){
            holder.characteristicNotifyAction.visibility = View.VISIBLE
        }

        //properties
        holder.characteristicProperties.text = "Properties : ${prop}"


        //ACTION BUTTONS

        //read
        holder.characteristicReadAction.setOnClickListener {
            this.readData(holder, characteristic)
        }

        //write
        holder.characteristicWriteAction.setOnClickListener {
            this.writeData(characteristic)
        }

        //notify
        holder.characteristicNotifyAction.setOnClickListener {

            //SUBSCRIPTION

            //toggle subscription
            if(enabled){

                //disable
                enabled = false
                gatt?.setCharacteristicNotification(characteristic, false)
            }else{

                //enable
                enabled = true
                gatt?.setCharacteristicNotification(characteristic, true)

                //for each descriptors ? <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                for(desc in characteristic.descriptors) {

                    //check if notification is part of the service properties
                    if( (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        desc.value =
                            if(enabled)
                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            else
                                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    }

                    //check if indication is part of the service properties
                    else if( (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        desc.value =
                            if(enabled)
                                BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                            else
                                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    }

                    //send descriptor
                    gatt?.writeDescriptor(desc)
                }
            }
        }


        //DATA TRANSFERS

        //receive data
        this.readData(holder, characteristic)

        //temporizing (for click event)
        //Thread.sleep(NOTIFY_DELAY)
    }



    //BLE BASICS

    //read
    private fun readData(
        holder         : CharacteristicViewHolder,
        characteristic : BluetoothGattCharacteristic
    ){
        msg.function("readData")

        //get data
        gatt?.readCharacteristic(characteristic)
        if(characteristic.value == null) {
            return
        }
        var receivedData = String(characteristic.value)

        //display read info
        if(characteristic.value != null){
            holder.characteristicValue.text = "value : $receivedData"
        }

        //set received data
        if(receivedData.length == 34) {

            //error code
            if(receivedData[3] == '2' && receivedData[4] == '3' && receivedData[5] == '2' && receivedData[6] == '8') {
                return
            }

            //correct value
            val lowestNegativeOn16b = 0x4000 //shall normally be 0x8000 (32768)$

            //Add a set in Kalman
            //If no element, first use => we init the new set with the current time to have a 0ms elapsed time
            var lastIndex = scene.kalmanFilter.dataSetList.size - 1
            if(lastIndex == -1){
                scene.kalmanFilter.dataSetList.add(DataSet(System.currentTimeMillis()))
            }
            //else, we pass the previous element current time to create an elapsed time
            else{
                scene.kalmanFilter.dataSetList.add(DataSet(scene.kalmanFilter.dataSetList[lastIndex].currentTime))
            }
            lastIndex++

            //ACC
            var uint16_value = fourHexToUInt16(receivedData[3], receivedData[4], receivedData[5], receivedData[6])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            uint16_value = fourHexToUInt16(receivedData[8], receivedData[9], receivedData[10], receivedData[11])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            uint16_value = fourHexToUInt16(receivedData[13], receivedData[14], receivedData[15], receivedData[16])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            //GYR
            uint16_value = fourHexToUInt16(receivedData[19], receivedData[20], receivedData[21], receivedData[22])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            uint16_value = fourHexToUInt16(receivedData[24], receivedData[25], receivedData[26], receivedData[27])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            uint16_value = fourHexToUInt16(receivedData[29], receivedData[30], receivedData[31], receivedData[32])
            scene.kalmanFilter.dataSetList[lastIndex].points.add(uint16ToInt16(uint16_value).toDouble())

            //Set new Kalman Filter condition
            scene.kalmanFilter.newDataSet = true
        }
    }



    //write
    private fun writeData(characteristic:BluetoothGattCharacteristic){
        msg.function("writeData")

        //get layout elements
        val alertDialog = AlertDialog.Builder(givenScene)
        val editView = View.inflate(givenScene, R.layout.popup_write, null)

        //dialog popup settings
        alertDialog.setView(editView)
        alertDialog.setPositiveButton("Done") { _, _ ->

            //get what user has input
            characteristic.value = editView.findViewById<EditText>(R.id.popup).text.toString().toByteArray()

            //send it through BLE
            gatt?.writeCharacteristic(characteristic)
        }
        alertDialog.setNegativeButton("Cancel") { alertDialog, _ -> alertDialog.cancel() }
        alertDialog.create()
        alertDialog.show()
    }




    //UTILITIES

    //conversions
    private fun hexToInt(c:Char) : Int {
        when(c){
            '1' -> return 1
            '2' -> return 2
            '3' -> return 3
            '4' -> return 4
            '5' -> return 5
            '6' -> return 6
            '7' -> return 7
            '8' -> return 8
            '9' -> return 9
            'a' -> return 10
            'b' -> return 11
            'c' -> return 12
            'd' -> return 13
            'e' -> return 14
            'f' -> return 15
            else -> return 0
        }
    }

    private fun fourHexToUInt16(hex1:Char, hex2:Char, hex3:Char, hex4:Char) : Int = (
        4096 * hexToInt(hex1) +
        256  * hexToInt(hex2) +
        16   * hexToInt(hex3) +
               hexToInt(hex4)
    )

    private fun uint16ToInt16(value:Int) : Int = -(value and 0x07ff)

    private fun byteArrayToHexString(array: ByteArray): String {
        msg.function("byteArrayToHexString")
        val result = StringBuilder(array.size * 2)

        //append character per character
        for (byte in array) {
            val toAppend = String.format("%X", byte)
            result.append(toAppend).append("-")
        }
        result.setLength(result.length - 1)

        return result.toString()
    }

    //availability details on screen
    private fun setPropertiesString(property:Int) : StringBuilder {
        msg.function("setPropertiesString")

        val sb = StringBuilder()
        if( (property and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
            sb.append("Write")
        }
        if( (property and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
            sb.append(" Read")
        }
        if( (property and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            sb.append(" Notify")
        }
        if(sb.isEmpty())
            sb.append("Nothing")

        return sb
    }




    //CONSTANTS

    //UUID attributes
    enum class BLEUUIDAttribute(val uuid:String, val title:String) {
        ACCES_GENERIQUE   ("00001800-0000-1000-8000-00805f9b34fb", "Accès générique"),
        ATTRIBUT_GENERIQUE("00001801-0000-1000-8000-00805f9b34fb", "Attribut générique"),
        SERVICE_SPECIFIQUE("466c1234-f593-11e8-8eb2-f2801f1b9fd1", "Service Spécifique "),
        SERVICE_SPE2      ("466c9abc-f593-11e8-8eb2-f2801f1b9fd1", "Service Spécifique2 "),
        UNKNOW_SERVICE    ("",                                     "Unknown");

        companion object {
            fun getBLEAttributeFromUUID(uuid:String) = values().firstOrNull { it.uuid == uuid } ?: UNKNOW_SERVICE
        }
    }
}
