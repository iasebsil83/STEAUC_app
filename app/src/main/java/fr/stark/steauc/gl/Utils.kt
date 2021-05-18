package fr.stark.steauc.gl

import android.content.Context
import java.util.*



class Utils {



    //useful
    companion object {

        //files
        fun readFile(
            context:Context,
            path:String,
            action:(String) -> Unit
        ){
            //open asset file
            val scanner = Scanner(context.assets.open(path))

            //for each line
            while(scanner.hasNextLine()){

                //do something with it
                action( scanner.nextLine() )
            }

            //close file
            scanner.close()
        }
    }
}
