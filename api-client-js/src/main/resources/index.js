'use strict'

// normally it would be assigned to variable and then used for module exports
// but scala.js does only export to global scope regardless of config
require('./lib/index.js');

module.exports = {
  Video: fun.scala.Video,
  Client: fun.scala.Client
}
