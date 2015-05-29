# KeyLogger
target SDK 17. No root

###### About

Custom keyboard from appcompat with some options.
- Logging - whenever target lanch keyboard, in background start process that send plain HTTP to server for initialization. Send new request every 20 seconds. When target press any key, capturing happens and new task prepare to be send;
- SMS - By request from server, grab all sms on device. Send date via HTTP;
- Contacts - Same as SMS, but for contact book;
- GPS Location - Just simple coordinates;
- Device info - calling afte init, take necessary information about device;
- Photo - create picture, store it, and send later via socket;
- Video - same as Photo (defaut length 4 sec);
- Phone call record - enabled by default. Start recording when target answer the call/ create call. Store data in DB, then send throught socket;

