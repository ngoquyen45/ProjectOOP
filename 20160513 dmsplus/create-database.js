conn = new Mongo("192.168.99.184:27017");
db = conn.getDB("salesquick2");

db.createCollection("Area");
db.createCollection("CalendarConfig");
db.createCollection("Config");
db.createCollection("Customer");
db.createCollection("CustomerType");
db.createCollection("Distributor");
db.createCollection("Product");
db.createCollection("ProductCategory");
db.createCollection("Promotion");
db.createCollection("Route");
db.createCollection("Survey");
db.createCollection("Target");
db.createCollection("UOM");
db.createCollection("User");
db.createCollection("VisitAndOrder");
db.createCollection("ExchangeReturn");
db.createCollection("PriceList");
db.createCollection("FileMetadata");
db.createCollection("fs.chunks");
db.createCollection("fs.files");

db.getCollection('User').insert(
  {
    "_class" : "com.viettel.persistence.mongo.domain.User",
    "defaultAdmin" : true,
    "usernameFull" : "master",
    "username" : "master",
    "fullname" : "master",
    "searchFullname": "master",
    "password" : "$2a$10$kTBdfDajI8f0GXwRGkTbfueJkq2AnDKUtpG3zZD3X9D92SmTycfmi",
    "role" : "SUPPORTER",
    "clientId" : ObjectId("000000000000000000000000"),
    "draft" : false,
    "active" : true
  }
);

db.getCollection('Config').insert(
  {
    "_id" : ObjectId("000000000000000000000000"), 
    "_class" : "com.viettel.persistence.mongo.domain.Config", 
    "dateFormat" : "dd/MM/yyyy", 
    "location" : {
        "latitude" : 21.017694697246966, 
        "longitude" : 105.78222513198853
    }, 
    "firstDayOfWeek" : NumberInt(2), 
    "minimalDaysInFirstWeek" : NumberInt(1), 
    "numberWeekOfFrequency" : NumberInt(1), 
    "numberDayOrderPendingExpire" : NumberInt(90), 
    "orderDateType" : "CREATED_DATE", 
    "visitDurationKPI" : NumberLong(0), 
    "visitDistanceKPI" : 0.0, 
    "canEditCustomerLocation" : false, 
    "clientId" : ObjectId("000000000000000000000000"), 
    "draft" : false, 
    "active" : true
  }
);

db.getCollectionNames().forEach(function(collection) {
  indexes = db[collection].getIndexes();
  if (collection == 'CalendarConfig'
    || collection == 'Config'
    || collection == 'CustomerType'
    || collection == 'Distributor'
    || collection == 'Product'
    || collection == 'ProductCategory'
    || collection == 'UOM'
    || collection == 'FileMetadata') {
      print("Add (clientId) index to " + collection);
      db[collection].createIndex( { "clientId" : 1 } );
  }

  if (collection == 'Area'
    || collection == 'Route'){
    print("Add (clientId + distributor._id) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "distributor._id" : 1 } );
  }

  if (collection == 'PriceList'){
    print("Add (clientId + distributorId) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "distributorId" : 1 } );
  }

  if (collection == 'Target'){
    print("Add (clientId + year + month + salesman._id) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "year" : -1, "month": -1, "salesman._id": 1 } );
  }

  if (collection == 'ExchangeReturn'){
    print("Add (clientId + distributor._id + createdTime) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "distributor._id": 1, "createdTime": 1 } );
  }

  if (collection == 'Promotion'
    || collection == 'Survey'){
    print("Add (clientId + draft + startDate + endDate) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "draft" : -1, "startDate": -1, "endDate": -1 } );
  }

  if (collection == 'User'){
    print("Add (clientId + role + distributor._id) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "role": 1, "distributor._id": 1 } );

    print("Add (usernameFull) index to " + collection);
    db[collection].createIndex( { "usernameFull" : "text" } );
  }

  if (collection == 'Customer') {
    print("Add (clientId + distributor._id + approveStatus + draft) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "approveStatus": 1, "distributor._id" : 1 } );
    db[collection].createIndex( { "clientId" : 1, "distributor._id" : 1, "schedule.routeId": 1 } );
    db[collection].createIndex( { "clientId" : 1, "customerType._id" : 1 } );
    db[collection].createIndex( { "clientId" : 1, "area._id" : 1 } );
  }

  if (collection == 'VisitAndOrder') {
    print("Add (clientId + distributor._id + startTime.value + approveStatus) index to " + collection);
    db[collection].createIndex( { "clientId" : 1, "distributor._id" : 1, "startTime.value" : -1, "createdBy._id": 1 } );
    
    db[collection].createIndex( { "clientId" : 1, "isOrder": 1, "distributor._id" : 1, "startTime.value" : -1 } );
    db[collection].createIndex( { "clientId" : 1, "isOrder": 1, "approveStatus": 1, "distributor._id" : 1 } );
    
    db[collection].createIndex( { "clientId" : 1, "isVisit": 1, "distributor._id" : 1, "startTime.value" : -1 } );
    db[collection].createIndex( { "clientId" : 1, "isFeedback" : 1, "distributor._id" : 1, "feedbacksReaded" : 1 } );
    db[collection].createIndex( { "clientId" : 1, "surveyAnswers.surveyId" : 1, "distributor._id" : 1 } );
  }

});
