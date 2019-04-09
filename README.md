# iCalExchange365

iCalExchange365 is a Rest API to download an iCalendar file with the events from a Microsoft Office 365 Calendar.
It allows to sync an Outlook calendar with a Google Calendar, for instance.

## Prerequisites

Before you begin, ensure you have met the following requirements:
* A Redis instance running (needed to store the Access Token of the Microsoft Graph API).

## Installation

To install, follow these steps:

Linux and macOS:
```
./mvnw clean install
```

Windows:
```
mvnw.cmd clean install
```

## Configuration

The following environment variables need to be set:
- `REDIS_URL` -- the URL of the redis instance (defaults to redis://user:pass@localhost:6379)
- `OAUTH_REDIRECT_URL` -- the URL to redirect to after OAuth authentication (e.g. https://localhost/authorize; defaults to https://login.microsoftonline.com/common/oauth2/nativeclient)
- `CLIENT_ID` -- the Application ID that the registration portal) assigned your app. (https://docs.microsoft.com/en-us/graph/auth-v2-user)
- `CLIENT_SECRET` -- the application secret that you created in the app registration portal for your app.

## Run

To run the project do:
```
./mvnw spring-boot:run
```

## Usage

### Authorize App

The first thing to do is to authorize the App to access the user's calendar.
To do this open the `/authorize` endpoint in the browser.
The optional parameter `userId` may be used to identify the user calling the API (e.g. `/authorize?userId=user1`).
If only one users will use the API then there is no need for the parameter `userId`.

The browser will be redirected to the Microsoft login page.
After the user logs in and authorizes the app, the browser will be automatically redirected back to the App endpoint with an access code.
After the App has fetched the access token it can call the Microsoft Graph API on behalf of the user.
Even after the access token expires the App will renew it automatically until the authorization is revoked.

### Fetch Calendar Appointments

The main API to call is the `/calendar` endpoint.
This endpoint returns the calendar's appointments in iCalendar format (RFC 5545).
Before calling the `/calendar` endpoint, the App must be authorized to access the user calendar by calling the `/authorize` endpoint as described above.
After the App is authorized, we can call the `/calendar` endpoint, using with the same `userId` (e.g. `/calendar?userId=user1`), to download the ICS file with the events in the user's calendar.
The optional parameters `startDateTme` and `endDateTime` may be used to filter the events retrieved to be inside the specific time range.
For example, `/calendar?userId=user1&startDateTime=2019-08-06T07:00:00.0&endDateTime=2019-08-06T20:00:00` or `/calendar?userId=user1&startDateTime=2019-08-01&endDateTime=2019-09-01`.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

To contribute to <project_name>, follow these steps:

1. Fork this repository.
2. Create a branch: `git checkout -b <branch_name>`.
3. Make your changes and commit them: `git commit -m '<commit_message>'`
4. Push to the original branch: `git push origin <project_name>/<location>`
5. Create the pull request.

Alternatively see the GitHub documentation on [creating a pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request).

## License

This project uses the following license: [MIT license](http://en.wikipedia.org/wiki/MIT_License).

## Source Code

The source code of this project is available at: https://github.com/edomingues/icalexchange365.
