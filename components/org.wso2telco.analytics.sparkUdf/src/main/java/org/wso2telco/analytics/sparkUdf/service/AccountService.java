package org.wso2telco.analytics.sparkUdf.service;

import org.killbill.billing.client.KillBillClient;
import org.killbill.billing.client.KillBillClientException;
import org.killbill.billing.client.KillBillHttpClient;
import org.killbill.billing.client.RequestOptions;
import org.killbill.billing.client.model.Account;
import org.killbill.billing.client.model.Bundle;
import org.wso2telco.analytics.sparkUdf.configProviders.ConfigurationDataProvider;
import org.wso2telco.analytics.sparkUdf.exception.KillBillException;

import java.util.UUID;

public class AccountService {

    public String addAccount(String user, String reason, String comment, String name, String currency, String externalKey, int nameL) {
        Account account = null;
        KillBillHttpClient killBillHttpClient = null;
        KillBillClient killBillClient = null;

        try {
            ConfigurationDataProvider dataProvider = ConfigurationDataProvider.getInstance();

            killBillHttpClient = new KillBillHttpClient(dataProvider.getUrl(), dataProvider.getUname(),
                    dataProvider.getPassword(), dataProvider.getApiKey(), dataProvider.getApiSecret());

            killBillClient = new KillBillClient(killBillHttpClient);
            
            account = createAccount(user, reason, comment, name, currency, externalKey, nameL, killBillClient);
           
        } catch (Exception e) {
            return "Did not created";
        } finally {
            closeKillBillClients(killBillHttpClient, killBillClient);
        }
        if (account!=null) {
        	return account.getAccountId().toString();
		} else {
			 return "Did not created";
		}
        
    }

    private Account getAccount(String accountId) throws KillBillException {
        KillBillHttpClient killBillHttpClient = null;
        KillBillClient killBillClient = null;

        try {
            ConfigurationDataProvider dataProvider = ConfigurationDataProvider.getInstance();

            killBillHttpClient = new KillBillHttpClient(dataProvider.getUrl(), dataProvider.getUname(),
                    dataProvider.getPassword(), dataProvider.getApiKey(), dataProvider.getApiSecret());

            killBillClient = new KillBillClient(killBillHttpClient);

            return killBillClient.getAccount(UUID.fromString(accountId), RequestOptions.empty());
        } catch (Exception e) {
            throw new KillBillException("Error occurred while getting account", e);
        } finally {
            closeKillBillClients(killBillHttpClient, killBillClient);
        }
    }

    @SuppressWarnings("deprecation")
    public String addSubAccount(String perentAcountId, String user, String reason, String comment, String name, String currency, String externalKey, int nameL) {
        Account account = null;
        KillBillHttpClient killBillHttpClient = null;
        KillBillClient killBillClient = null;

        try {
            ConfigurationDataProvider dataProvider = ConfigurationDataProvider.getInstance();

            killBillHttpClient = new KillBillHttpClient(dataProvider.getUrl(),
                    dataProvider.getUname(),
                    dataProvider.getPassword(),
                    dataProvider.getApiKey(),
                    dataProvider.getApiSecret());

            killBillClient = new KillBillClient(killBillHttpClient);

            account = createAccount(perentAcountId, user, reason, comment, name, currency, externalKey, nameL, killBillClient);
        } catch (Exception e) {
            return "Child did not created";
        } finally {
            if (killBillClient != null) {
                killBillClient.close();
            }
            if (killBillHttpClient != null) {
                killBillHttpClient.close();
            }
        }

        if (account!=null) {
        	return account.getAccountId().toString();
		} else {
			 return "Child did not created";
		}
    }

    private Account createAccount(String perentAcountId, String user, String reason, String comment, String name, String currency, String externalKey, int nameL, KillBillClient killBillClient) throws KillBillClientException {
        Account account;
        account = new Account();
        account.setName(name);
        account.setFirstNameLength(nameL);
        account.setExternalKey(externalKey);
        account.setCurrency(currency);
        account.setParentAccountId(UUID.fromString(perentAcountId));
        account = killBillClient.createAccount(account, user, reason, comment);
        return account;
    }

    private Account createAccount(String user, String reason, String comment, String name, String currency, String externalKey, int nameL, KillBillClient killBillClient) throws KillBillClientException {
        Account account;
        account = new Account();
        account.setName(name);
        account.setFirstNameLength(nameL);
        account.setExternalKey(externalKey);
        account.setCurrency(currency);
        account = killBillClient.createAccount(account, user, reason, comment);
        return account;
    }

    private void closeKillBillClients(KillBillHttpClient killBillHttpClient, KillBillClient killBillClient) {
        if (killBillClient != null) {
            killBillClient.close();
        }
        if (killBillHttpClient != null) {
            killBillHttpClient.close();
        }
    }
}
