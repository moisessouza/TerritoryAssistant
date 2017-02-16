package ged.mediaplayerremote.data.network;

import ged.mediaplayerremote.data.entity.ServerEntity;
import org.jsoup.Jsoup;
import rx.Subscriber;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A {@link Runnable} implementation, that when executed, will check whether there is valid MPC-HC server at given
 * IP, and pass the server to the provided {@link Subscriber}. If server is not found, null will be passed.
 */
public class IpScan implements Runnable
{
    private String ip;
    private String port;
    private Subscriber<? super ServerEntity> subscriber;
    private int timeout;

    public IpScan(String ip, String port, int timeout, Subscriber<? super ServerEntity> subscriber)
    {
        this.ip = ip;
        this.port = port;
        this.subscriber = subscriber;
        this.timeout = timeout;
    }

    @Override
    public void run()
    {
        if (this.serverFound())
        {
            try
            {
                ServerEntity serverEntity = createServerEntity();
                subscriber.onNext(serverEntity);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
        else{
            subscriber.onNext(null);
        }


        subscriber.onCompleted();
    }

    private boolean serverFound()
    {
        String url = "http://" + ip + ":" + port + "/info.html";
        try
        {
            Jsoup.connect(url).timeout(timeout).get();
            return true;

        } catch (IOException e)
        {
            return false;
        }
    }

    private ServerEntity createServerEntity() throws UnknownHostException
    {
        ServerEntity serverEntity = new ServerEntity();
        serverEntity.setIp(ip);
        serverEntity.setPort(port);
        serverEntity.setConnectionTimeout(timeout);

        String hostname = InetAddress.getByName(ip).getHostName();

        serverEntity.setHostName(hostname);

        return serverEntity;
    }
}
