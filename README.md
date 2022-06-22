# hey-sharing-scala

A service that allows you to rent and lease things instead of buying/selling them completely from other people

## Features

1. Create Item (POST -> localhost:8080/items)
```sh
{
      "name": "Mashina",
      "price": 228,
      "image": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
      "description": "Vishnevaya semerka neonovie fari",
      "category": "Other",
      "address": "BC Urban"
}
```

2. Update Item (PUT -> localhost:8080/items)
```sh
{
      "id": 1
      "name": "Renault logan",
      "price": 322,
      "image": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
      "description": "Belogo cveta 20-go veka",
      "category": "Other",
      "address": "BC Vodnii"
}
```

3. Take a rent (POST -> localhost:8080/rent/toTenant/{itemId})
```sh
{
      "periodType": "Week",
      "countOfPeriod": 4,
      "productID": 2
}
```

## Getiing started

1. Git clone this project
2. ```sh
   docker compose up
   ```
3. Press green triangle in IDEA =)

