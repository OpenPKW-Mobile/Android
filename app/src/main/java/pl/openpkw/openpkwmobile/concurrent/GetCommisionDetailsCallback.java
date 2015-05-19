package pl.openpkw.openpkwmobile.concurrent;

import pl.openpkw.openpkwmobile.models.CommissionDetails;

/**
 * Created by michalu on 19.05.15.
 */
public interface GetCommisionDetailsCallback {
    /**
     * called when the commission details were successfully obtained from the API
     * @param commissionDetails
     */
    public void onSuccess(CommissionDetails commissionDetails);

    /**
     * called when something gone wrong while communicating with the API
     * @param e
     */
    public void onError(Exception e);
}
