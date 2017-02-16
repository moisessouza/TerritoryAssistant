package ged.mediaplayerremote.data.network;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import ged.mediaplayerremote.data.R;
import ged.mediaplayerremote.data.entity.ServerEntity;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import rx.Observable;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A class that scans all IP addresses in current Wi-Fi, searching for available MPC-HC servers.
 */
public class ServerFinder {

    private static final int MAX_THREADS = 26;

    private String devicesIP;
    private WifiManager wifiManager;
    private Resources resources;
    private UserPreferencesRepository userPreferencesRepository;

    @Inject
    public ServerFinder(Context context, UserPreferencesRepository userPreferencesRepository) {
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.resources = context.getResources();
        this.userPreferencesRepository = userPreferencesRepository;
    }

    /**
     * Get a {@link Observable} which will emit every available {@link ServerEntity} in a current Wi-Fi network.
     */
    public Observable<ServerEntity> getServers() {
        try {
            return Observable.merge(getServerScans())
                    .zipWith(Observable.interval(50, TimeUnit.MILLISECONDS).onBackpressureDrop(), (serverEntity, aLong) -> serverEntity);
        } catch (UnknownHostException ex) {
            String errorMsg = resources.getString(R.string.error_no_wifi);
            return Observable.error(new IOException(errorMsg, ex));
        }
    }

    private Observable<ServerEntity>[] getServerScans() throws UnknownHostException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        devicesIP = wifiIPAddress();
        @SuppressWarnings("unchecked")
        Observable<ServerEntity>[] observables = new Observable[256];
        int phoneIpFourthBlock = getFourthIpBlock(devicesIP);
        int fourthIpBlockToScan;

        if (phoneIpFourthBlock > 100) {
            fourthIpBlockToScan = 100;
        } else {
            fourthIpBlockToScan = 0;
        }

        String port = userPreferencesRepository.getPort();
        int counter = 0;
        while (counter < 256) {
            final int fourthIPBlock = fourthIpBlockToScan;
            observables[counter] = Observable.create(subscriber ->
            {
                String ipToScan = getFullIp(fourthIPBlock);
                Runnable ipScan = new IpScan(ipToScan, port,
                        userPreferencesRepository.getConnectionTimeout(), subscriber);
                threadPoolExecutor.execute(ipScan);
            });
            fourthIpBlockToScan++;
            if (fourthIpBlockToScan == 256) {
                fourthIpBlockToScan = 0;
            }
            counter++;
        }

        return observables;
    }

    private String getFullIp(int fourthBlock) {
        StringTokenizer tokenizer = new StringTokenizer(this.devicesIP, ".");
        String IP = "";
        for (int i = 0; i < 3; i++) {
            IP += tokenizer.nextToken() + ".";
        }
        IP += fourthBlock;
        return IP;
    }

    private String wifiIPAddress() throws UnknownHostException {
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
        String ipAddressString;
        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        return ipAddressString;
    }

    private int getFourthIpBlock(String ip) {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        return Integer.parseInt(tokenizer.nextToken());
    }


}
