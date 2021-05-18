package fr.stark.steauc.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import fr.stark.steauc.R
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Message
import fr.stark.steauc.log.Error








class BLEServiceAdapter(
    private val gatt        : BluetoothGatt?,
    private val serviceList : MutableList<BLEService>,
    private val context     : Context
) : ExpandableRecyclerViewAdapter<
        BLEServiceAdapter.ServiceViewHolder,
        BLEServiceAdapter.CharacteristicViewHolder
>(serviceList) {








    //debug info
    private val info : CodeInfo = CodeInfo("BLEService", "BLEServiceAdapter.kt")
    private val msg  : Message  = Message(info)
    private val err  : Error    = Error  (info)

    //notification subscription
    private var enabled : Boolean = false








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
        info.setFunctionName("onBindChildViewHolder")
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
        Thread.sleep(100)
    }








    //BLE BASICS

    //read
    private fun readData(
        holder         : BLEServiceAdapter.CharacteristicViewHolder,
        characteristic : BluetoothGattCharacteristic
    ){
        //get data
        gatt?.readCharacteristic(characteristic)

        //display read info
        if (characteristic.value != null) {
            holder.characteristicValue.text = "value : ${String(characteristic.value)}"
        }
    }



    //write
    private fun writeData(
        characteristic : BluetoothGattCharacteristic
    ){

        //get layout elements
        val alertDialog = AlertDialog.Builder(context)
        val editView = View.inflate(context, R.layout.popup_write, null)

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
    private fun byteArrayToHexString(array: ByteArray): String {
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
    private fun setPropertiesString(property: Int): StringBuilder {
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
    enum class BLEUUIDAttribute(val uuid: String, val title: String) {
        ACCES_GENERIQUE   ("00001800-0000-1000-8000-00805f9b34fb", "Accès générique"),
        ATTRIBUT_GENERIQUE("00001801-0000-1000-8000-00805f9b34fb", "Attribut générique"),
        SERVICE_SPECIFIQUE("466c1234-f593-11e8-8eb2-f2801f1b9fd1", "Service Spécifique "),
        SERVICE_SPE2      ("466c9abc-f593-11e8-8eb2-f2801f1b9fd1", "Service Spécifique "),
        UNKNOW_SERVICE    ("",                                     "Unknown");

        companion object {
            fun getBLEAttributeFromUUID(uuid: String) = values().firstOrNull { it.uuid == uuid } ?: UNKNOW_SERVICE
        }
    }
}
