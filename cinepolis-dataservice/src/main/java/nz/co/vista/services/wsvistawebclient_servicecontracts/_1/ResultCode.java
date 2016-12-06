
package nz.co.vista.services.wsvistawebclient_servicecontracts._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResultCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="UnexpectedError"/>
 *     &lt;enumeration value="DBError"/>
 *     &lt;enumeration value="MembershipError"/>
 *     &lt;enumeration value="InvalidRequestData"/>
 *     &lt;enumeration value="CinemaConnectError"/>
 *     &lt;enumeration value="LoyaltyConnectError"/>
 *     &lt;enumeration value="InsufficientPoints"/>
 *     &lt;enumeration value="SeatsUnavailable"/>
 *     &lt;enumeration value="FailedSeatDataRetrieve"/>
 *     &lt;enumeration value="FailedOrderValueProcess"/>
 *     &lt;enumeration value="PrepareOrderForCompleteError"/>
 *     &lt;enumeration value="PaymentDeclined"/>
 *     &lt;enumeration value="PaymentSystemError"/>
 *     &lt;enumeration value="PostChargeCommitError"/>
 *     &lt;enumeration value="UnpaidBookingsDisallowed"/>
 *     &lt;enumeration value="VouchersDisallowed"/>
 *     &lt;enumeration value="MemberNotFound"/>
 *     &lt;enumeration value="MemberPasswordIncorrect"/>
 *     &lt;enumeration value="LoyaltyTimeout"/>
 *     &lt;enumeration value="LoyaltyUserNameAlreadyExists"/>
 *     &lt;enumeration value="LoyaltyNotSupportedAtCinema"/>
 *     &lt;enumeration value="MemberEmailIncorrect"/>
 *     &lt;enumeration value="MemberLoginResetDisallowed"/>
 *     &lt;enumeration value="PaymentSuccessWithErrors"/>
 *     &lt;enumeration value="PaymentVoidSuccessPostCommit"/>
 *     &lt;enumeration value="PaymentVoidSuccessPreCommit"/>
 *     &lt;enumeration value="PaymentVoidSuccessPayTotalMismatch"/>
 *     &lt;enumeration value="FailedToAllocateCard"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ResultCode")
@XmlEnum
public enum ResultCode {

    OK("OK"),
    @XmlEnumValue("UnexpectedError")
    UNEXPECTED_ERROR("UnexpectedError"),
    @XmlEnumValue("DBError")
    DB_ERROR("DBError"),
    @XmlEnumValue("MembershipError")
    MEMBERSHIP_ERROR("MembershipError"),
    @XmlEnumValue("InvalidRequestData")
    INVALID_REQUEST_DATA("InvalidRequestData"),
    @XmlEnumValue("CinemaConnectError")
    CINEMA_CONNECT_ERROR("CinemaConnectError"),
    @XmlEnumValue("LoyaltyConnectError")
    LOYALTY_CONNECT_ERROR("LoyaltyConnectError"),
    @XmlEnumValue("InsufficientPoints")
    INSUFFICIENT_POINTS("InsufficientPoints"),
    @XmlEnumValue("SeatsUnavailable")
    SEATS_UNAVAILABLE("SeatsUnavailable"),
    @XmlEnumValue("FailedSeatDataRetrieve")
    FAILED_SEAT_DATA_RETRIEVE("FailedSeatDataRetrieve"),
    @XmlEnumValue("FailedOrderValueProcess")
    FAILED_ORDER_VALUE_PROCESS("FailedOrderValueProcess"),
    @XmlEnumValue("PrepareOrderForCompleteError")
    PREPARE_ORDER_FOR_COMPLETE_ERROR("PrepareOrderForCompleteError"),
    @XmlEnumValue("PaymentDeclined")
    PAYMENT_DECLINED("PaymentDeclined"),
    @XmlEnumValue("PaymentSystemError")
    PAYMENT_SYSTEM_ERROR("PaymentSystemError"),
    @XmlEnumValue("PostChargeCommitError")
    POST_CHARGE_COMMIT_ERROR("PostChargeCommitError"),
    @XmlEnumValue("UnpaidBookingsDisallowed")
    UNPAID_BOOKINGS_DISALLOWED("UnpaidBookingsDisallowed"),
    @XmlEnumValue("VouchersDisallowed")
    VOUCHERS_DISALLOWED("VouchersDisallowed"),
    @XmlEnumValue("MemberNotFound")
    MEMBER_NOT_FOUND("MemberNotFound"),
    @XmlEnumValue("MemberPasswordIncorrect")
    MEMBER_PASSWORD_INCORRECT("MemberPasswordIncorrect"),
    @XmlEnumValue("LoyaltyTimeout")
    LOYALTY_TIMEOUT("LoyaltyTimeout"),
    @XmlEnumValue("LoyaltyUserNameAlreadyExists")
    LOYALTY_USER_NAME_ALREADY_EXISTS("LoyaltyUserNameAlreadyExists"),
    @XmlEnumValue("LoyaltyNotSupportedAtCinema")
    LOYALTY_NOT_SUPPORTED_AT_CINEMA("LoyaltyNotSupportedAtCinema"),
    @XmlEnumValue("MemberEmailIncorrect")
    MEMBER_EMAIL_INCORRECT("MemberEmailIncorrect"),
    @XmlEnumValue("MemberLoginResetDisallowed")
    MEMBER_LOGIN_RESET_DISALLOWED("MemberLoginResetDisallowed"),
    @XmlEnumValue("PaymentSuccessWithErrors")
    PAYMENT_SUCCESS_WITH_ERRORS("PaymentSuccessWithErrors"),
    @XmlEnumValue("PaymentVoidSuccessPostCommit")
    PAYMENT_VOID_SUCCESS_POST_COMMIT("PaymentVoidSuccessPostCommit"),
    @XmlEnumValue("PaymentVoidSuccessPreCommit")
    PAYMENT_VOID_SUCCESS_PRE_COMMIT("PaymentVoidSuccessPreCommit"),
    @XmlEnumValue("PaymentVoidSuccessPayTotalMismatch")
    PAYMENT_VOID_SUCCESS_PAY_TOTAL_MISMATCH("PaymentVoidSuccessPayTotalMismatch"),
    @XmlEnumValue("FailedToAllocateCard")
    FAILED_TO_ALLOCATE_CARD("FailedToAllocateCard");
    private final String value;

    ResultCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResultCode fromValue(String v) {
        for (ResultCode c: ResultCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
