# Presentation layer 
- Presentation layer is an user interface or a communication layer of the application where allows user or another application interact with. 
    - Ex: APIs, UI on mobile app, web apps.

- This layer is the place that handle how to show application data, open a gate to interact with and handle presentation logic. 
    - Ex: What colors to show the complete status of a todo or showing a loading UI when user modified a todo, decide how the data structure in response of an API server.

# Domain layer
- Domain layer is responsible for encapsulating complex business logic, or simple business logic for reuse.

- Domain layer help us to:
    - Avoid code duplication
    - Easier to test
    - Easier to read and maintain

# Data layer
- Data layer is responsible for request or modify application data.
- This layer is constructed by repositories and data sources
## Repository pattern

- Repository pattern is a communicator between domain layer and data source.

- Repositories help us to expose data APIs. Domain layer classes will use repositories to request or modify data that it need.

- Every repository needs data sources as dependencies to request data from internet or database.

- Ex: PostRemoteDataSource, UserRemoteDataSource

- Benefits of repository pattern:
    - Easier to test (Mock data)
    - We can change data sources that use inside the repository without rewrite the whole domain layer code where use that data.

## Data source

- Data source is responsible for work with one source for a specific type of data. 
- A data source will receive a DAO (Data access object) as a parameter. 

