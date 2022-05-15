package pl.baluch.stickerprinter.plugins;

import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Plugin {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final String name;
    protected boolean exit = false;
    private final Map<String, ReflectionItemsSupplier> itemSuppliersByCategory = new HashMap<>();

    protected Plugin(String name) {
        this.name = name;
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if(declaredMethod.isAnnotationPresent(ItemsSupplier.class)){
                if(declaredMethod.getParameterCount() > 0){
                    System.err.println(declaredMethod.getName() + " should not have any parameters.");
                    continue;
                }
                if(!Collection.class.isAssignableFrom(declaredMethod.getReturnType())){
                    System.err.println(declaredMethod.getName() + " should return a collection.");
                    continue;
                }
                ItemsSupplier supplierInfo = declaredMethod.getAnnotation(ItemsSupplier.class);

                itemSuppliersByCategory.put(supplierInfo.category(), new ReflectionItemsSupplier(supplierInfo, declaredMethod));
            }
        }
    }

    public void addChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    protected void updateProducts(String category) {
        Platform.runLater(() -> support.firePropertyChange("products", null, category));
    }

    public String getName() {
        return this.name;
    }

    public List<Item> getItems(){
        return itemSuppliersByCategory.values().stream()
                .flatMap(supplier -> supplier.get(true).stream())
                .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList());
    }
    public List<Item> getItems(String category){
        if(!itemSuppliersByCategory.containsKey(category)){
            return new ArrayList<>();
        }
        return new ArrayList<>(itemSuppliersByCategory.get(category).get(false));
    }
    public Set<String> getCategories(){
        return itemSuppliersByCategory.keySet();
    }

    public void setExit() {
        this.exit = true;
    }

    private class ReflectionItemsSupplier {
        private final ItemsSupplier supplierInfo;
        private final Method declaredMethod;

        public ReflectionItemsSupplier(ItemsSupplier supplierInfo, Method declaredMethod) {
            this.supplierInfo = supplierInfo;
            this.declaredMethod = declaredMethod;
        }

        public Collection<Item> get(boolean forAll) {
            if(!forAll || supplierInfo.visibleInAll()){
                try {
                    return (Collection<Item>) declaredMethod.invoke(Plugin.this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return new ArrayList<>();
        }
    }
}
