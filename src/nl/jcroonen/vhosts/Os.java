package nl.jcroonen.vhosts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Os {
    public static ArrayList<String> getOutput(Process process, String filter) throws IOException {
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        ArrayList<String> arrayList = new ArrayList<>();
        String s;
        while ((s = stdInput.readLine()) != null) {
            if (filter == null || s.contains(filter)) {
                arrayList.add(s);
            }
        }
        return arrayList;
    }

    public static void execute(String[] args, Consumer<String> callback) {
        AtomicReference<Runtime> runtime = new AtomicReference<>(Runtime.getRuntime());
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    runtime.get().exec(args);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            future.get();
            callback.accept("finished");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String[] args) {
        AtomicReference<Runtime> runtime = new AtomicReference<>(Runtime.getRuntime());
        try {
            runtime.get().exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getCmdOutput(String[] args, String filter) {
        AtomicReference<Runtime> runtime = new AtomicReference<>(Runtime.getRuntime());
        try
        {
            Process process = runtime.get().exec(args);
            return Os.getOutput(process, filter);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static ArrayList<String> getLaunchAgentsList(String filter) {
        String[] args = {"launchctl", "list"};
        return getCmdOutput(args, filter);
    }

    public static ArrayList<String> getServiceFiles(String filter) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        String[] args = {"ls", bundle.getString("launchagentsdir")};
        return getCmdOutput(args, filter);
    }

}
