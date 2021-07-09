package DynamicBandwidthManagement;

import java.util.ArrayList;

public class Leftovers {
    ArrayList<Group> groupsOfUsers = new ArrayList<Group>();

    private double calculateA(Group group){
        double result = 0;
        Group currentGroup;

        for(int i=0; i<groupsOfUsers.size(); ++i){
            currentGroup = groupsOfUsers.get(i);

            if(currentGroup.equals(group))
                continue;
            else
                result += (getAk(currentGroup) * currentGroup.qi);
        }

        result += ((group.getConnectedUsers()+1)/2 + (group.getConnectedUsers()+1)%2);

        return result;
    }
    private double calculateB(){
        double result = 0;
        Group currentGroup;

        for(int i=0; i<groupsOfUsers.size(); ++i){
            currentGroup = groupsOfUsers.get(i);
            result += (getAk(currentGroup) * currentGroup.qi);
        }

        return result;
    }
    private int getAk(Group group){
        int Ak = 0;
        int size = group.getConnectedUsers();

        if(size == 0){
            Ak = 1;
        } else {
            Ak = size/2 + size%2;
        }

        return Ak;
    }

    // Find Minimal Positive Root
    public double findMinimalPositiveRoot(){
        double root = 0;
        int size = groupsOfUsers.size();
        double[] roots;
        double[] coeff = new double[size];

        // Construct the polynomial
        for(int k=0; k<size; ++k){
            coeff[k] = (getAk(groupsOfUsers.get(k)));
        }

        //oeff[0] -= G/g;

        roots = BirgeVieta(coeff, 1.5, size-1, size*2, 20, 0.05);

        double minimalPositiveRoot = Double.POSITIVE_INFINITY;
        for(int i=0; i<size*2; ++i){
            if(roots[i] > 0.8 && roots[i] < minimalPositiveRoot)
                minimalPositiveRoot = roots[i];
        }

        if(!(minimalPositiveRoot == Double.POSITIVE_INFINITY))
            root = Math.round(minimalPositiveRoot);

        return root;
    }
    // Find Roots Using Birge-Vieta Method
    private double[] BirgeVieta(double[] a, double x0, int nOrder, int nRoots, int nIterations, double tolerance) {
        double x = x0;
        double[] roots = new double[nRoots];
        double[] a1 = new double[nOrder + 1];
        double[] a2 = new double[nOrder + 1];
        double[] a3 = new double[nOrder + 1];
        for(int j = 0; j <= nOrder; j++)
            a1[j] = a[j];
        double delta = tolerance * 10;
        int i = 1, n = nOrder + 1, nroot = 0;
        while (i++ < nIterations && n > 1)
        {
            double x1 = x;
            a2[n-1] = a1[n-1];
            a3[n-1] = a1[n-1];
            for (int j = n - 2; j > 0; j--)
            {
                a2[j] = a1[j] + x1 * a2[j + 1];
                a3[j] = a2[j] + x1 * a3[j + 1];
            }
            a2[0] = a1[0] + x1 * a2[1];
            delta = a2[0] / a3[1];
            x -= delta;
            if(Math.abs(delta) < tolerance)
            {
                i = 1;
                n--;
                roots[nroot] = x;
                nroot++;
                for (int j = 0; j < n; j++)
                {
                    a1[j] = a2[j + 1];
                    if (n == 2)
                    {
                        n--;
                        roots[nroot] = -a1[0];
                        nroot++;
                    }
                }
            }
        }
        return roots;
    }

}
