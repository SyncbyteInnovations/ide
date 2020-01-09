package helper.utils.java;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import helper.logger.LogUtil;

public class FileUtil
{
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws Exception
    {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try
        {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally
        {
            try
            {
                if (fromChannel != null)
                {
                    fromChannel.close();
                }
                if (toChannel != null)
                {
                    toChannel.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static File getUsbRootDirectory(String otgPath, String directoryName)
    {
        File usbRootDirectory = null;
        File sdCard = new File(otgPath);
        File[] directoryArray = sdCard.listFiles();
        if (directoryArray != null)
        {
            for (File directory : directoryArray)
            {
                if (directory.getName().equalsIgnoreCase(directoryName))
                {
                    usbRootDirectory = directory;
                    break;
                }
            }
        }
        return usbRootDirectory;
    }

    public static File stringToFile(byte[] btDataArray)
    {
        File file = null;
        FileOutputStream oFileOutputStream = null;
        try
        {
            boolean valid = false;
            file = new File(Environment.getExternalStorageDirectory() + "/dummyFile.txt");
            if (!file.exists())
            {
                if (file.createNewFile())
                {
                    valid = true;
                }
            }
            else
            {
                valid = true;
            }

            if (valid)
            {
                oFileOutputStream = new FileOutputStream(file);
                oFileOutputStream.write(btDataArray);
                oFileOutputStream.flush();
            }
        } catch (Exception e)
        {
            LogUtil.logException("helper.utils.java.fileToDrive", e);
        } finally
        {
            try
            {
                if (oFileOutputStream != null)
                {
                    oFileOutputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static void fileToDrive(File usbRootDirectory, String strFileName, byte[] btDataArray)
    {
        File file = new File(usbRootDirectory.getAbsolutePath() + "/" + strFileName);
        FileOutputStream oFileOutputStream = null;
        try
        {
            boolean valid = false;
            if (!file.exists())
            {
                if (file.createNewFile())
                {
                    valid = true;
                }
            }
            else
            {
                valid = true;
            }

            if (valid)
            {
                oFileOutputStream = new FileOutputStream(file);
                oFileOutputStream.write(btDataArray);
                oFileOutputStream.flush();
            }
        } catch (Exception e)
        {
            LogUtil.logException("helper.utils.java.fileToDrive", e);
        } finally
        {
            try
            {
                if (oFileOutputStream != null)
                {
                    oFileOutputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}