package ServerCore.DefaultComponents.Interfaces.Template;

import ServerCore.ServerCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public abstract class Interface extends CoreComponent {
    public final InterfaceType interfaceType;
    private static ComponentType[] addX(ComponentType arr[], ComponentType x)
    {

        ComponentType newarr[] = new ComponentType[arr.length + 1];

        // insert the elements from
        // the old array into the new array
        // insert all elements till n
        // then insert x at n+1
        for (int i = 0; i < arr.length; i++)
            newarr[i] = arr[i];

        newarr[newarr.length - 1] = x;

        return newarr;
    }
    public Interface(String name, Boolean active, ServerCore core, InterfaceType interfType, ComponentType... type) {
        super(name, active, core, addX(type, ComponentType.INTERFACE));
        this.interfaceType = interfType;
    }
}
