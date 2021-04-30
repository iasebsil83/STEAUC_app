package fr.stark.steauc.ble

import android.bluetooth.le.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.stark.steauc.R
import fr.stark.steauc.databinding.LyoBleRecViewCellBinding
import fr.stark.steauc.log.CodeInfo
import fr.stark.steauc.log.Message



//binding
private lateinit var binding : LyoBleRecViewCellBinding



class BLEScanAdapter(
    private val scanList : MutableList<ScanResult>,
    private val onItemClickListener:(ScanResult) -> Unit
) : RecyclerView.Adapter<BLEScanAdapter.BLEScanViewHolder>() {

    //info
    private var info : CodeInfo = CodeInfo("BLEScanAdapter", "ble/BLEScanAdapter.kt")


    //init
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BLEScanAdapter.BLEScanViewHolder {

        //debug
        info.setFunctionName("onCreateViewHolder")
        Message(info).log("Created RecyclerViewCell.")

        return BLEScanViewHolder(
            LyoBleRecViewCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }



    //viewHolder
    class BLEScanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cellTitle   : TextView = view.findViewById(R.id.ble_cell_title)
        val cellContent : TextView = view.findViewById(R.id.ble_cell_content)
    }


    //utilities
    override fun getItemCount() = scanList.size



    //events
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BLEScanViewHolder, position: Int) {
        holder.cellTitle.text   = scanList[position].device.address
        holder.cellContent.text = scanList[position].toString()
        holder.cellTitle.setOnClickListener(){
            onItemClickListener(scanList[position])
        }
    }
}