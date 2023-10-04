# Togglz-Careem

A patched version of togglz library (2.6.1.Final) used for Careem purposes. Decision on 2.6.1 was taken instead of any other version due to the compatibility reasons at Careem legacy projects. Instead of JDBC Datasource, we will be using Galileo (Configuration and experimentation platform) to sync feature state.

There will be a sync mechanism which syncronises togglz db data to galileo storage. A periodic sync will be happening every hour./ Immediate option, if there is a way to sync the data to galileo on deman. 

Library origin based on [togglz](https://github.com/togglz/togglz)