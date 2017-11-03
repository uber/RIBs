Pod::Spec.new do |s|
  s.name             = 'RIBs'
  s.version          = '0.9.0'
  s.summary          = 'A short description of RIBs.'
  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC
  s.homepage         = 'https://github.com/uber/RIBs'
  s.license          = { :type => 'MIT', :file => '../LICENSE.txt' }
  s.author           = { 'uber' => 'mobile-open-source@uber.com' }
  s.source           = { :git => 'https://github.com/uber/RIBs.git', :tag => s.version.to_s }
  s.ios.deployment_target = '8.0'
  s.source_files = 'RIBs/Classes/**/*'
  s.dependency 'RxSwift', '~> 4.0'
end
