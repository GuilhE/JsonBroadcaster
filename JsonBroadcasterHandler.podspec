Pod::Spec.new do |s|
  s.name       = 'JsonBroadcasterHandler'
  s.version    = '1.0.0'
  s.summary    = 'Update your app\'s UI State at runtime.'

  s.homepage   = 'https://github.com/GuilhE/JsonBroadcaster'
  s.license    = { :type => 'Apache-2.0', :file => 'LICENSE' } 
  s.authors    = 'Guilherme Delgado'

  s.source = {
    :git => 'https://github.com/GuilhE/JsonBroadcaster.git',
    :tag => 'v' + s.version.to_s
  }

  s.swift_versions = ['5.5']
  s.ios.deployment_target = '13.0'

  s.frameworks = 'Combine', 'Foundation', 'UserNotifications'
  
  s.source_files = 'JsonBroadcasterHandler/**/*.swift'
end
