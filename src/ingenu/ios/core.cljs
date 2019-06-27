(ns ingenu.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [ingenu.events]
            [ingenu.subs]))

(def ReactNative (js/require "react-native"))
(def ReactNavigation (js/require "react-navigation"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))

(def create-tab-navigator (.-createBottomTabNavigator ReactNavigation))
(def create-stack-navigator (.-createStackNavigator ReactNavigation))
(def create-app-container (.-createAppContainer ReactNavigation))

(def linking (.-Linking ReactNative))

(def logo-img (js/require "./images/cropped-flame.png"))
(def ingenuity-url "ingenuitycleveland.com")

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(def basic-column {:style {:flex 1
                           :flex-direction "column"
                           :justify-content "space-evenly"
                           :align-items "center"}})
(def basic-row {:style {:flex-direction "row"
                        :margin 20}})

(defn nav-to
  [props nav-target]
  ((:navigate (:navigation props)) nav-target))

(defn nav-go-back
  [props]
  ((:goBack (js->clj (:navigation props) :keywordize-keys true))))

(defn nav-go-url
  [target]
  (.openURL linking (str "http://" target)))

(def centered-white-bold {:style {:color "white"
                                  :text-align "center"
                                  :font-weight "bold"}})

(def centered-list {:style {:color "black"
                            :text-align "center"}})

(defn submit-email-to-server
  [email])

(defn collect-email-screen []
  (let [email-input (r/atom "")
        reset-email #(reset! email-input "")
        submit-email #(do
                        (dispatch [:update-email-list @email-input])
                        (reset-email))]
    (fn []
      [view basic-column
       [view basic-row
        [text {:style {:font-size 36
                       :font-weight "500"
                       :text-align "center"}} "Enter your email"]]
       [view basic-row
        [view basic-column
         [text-input {:style {:font-size 32
                              :font-weight "100"
                              :text-align "center"
                              :border-color "gray"
                              :border-width 1}
                      :placeholder "flurry@example.org"
                      :on-blur reset-email
                      :on-change-text #(reset! email-input %)}
          @email-input]
         [touchable-opacity {:style {:background-color "#999"
                                     :padding 10
                                     :border-radius 5}
                             :on-press submit-email}
          [text {:style {:color "white"
                         :text-align "center"
                         :font-weight "bold"}}
           "Submit"]]]]
       [view basic-row
        [text {:style {:font-size 24
                       :font-weight "300"
                       :text-align "center"}}
         "You will receive approximately 1 email per week alerting you to events and happenings at Ingenuity. We will never share your email with any 3rd parties without your consent."]]])))

(def <sub  (comp deref re-frame.core/subscribe))

(defn home-screen []
  (let [greeting "Ingenuity Cleveland"]
    (fn [props]
      (let [props (js->clj props :keywordize-keys true)]
        [view basic-column
         [view basic-row
          [text {:style {:font-size 30
                         :font-weight "100"
                         :margin-bottom 20
                         :text-align "center"}} greeting]
          [image {:source logo-img
                  :style  {:width 80
                           :height 80
                           :margin-bottom 30}}]]
         [view basic-row
          [touchable-opacity {:style {:background-color "#999"
                                      :padding 10 :border-radius 5}
                              :on-press #(nav-to props "Modal")}
           [text centered-white-bold "Learn more about Ingenuity"]]]
         [view basic-row
          [touchable-opacity {:style {:background-color "#999"
                                      :padding 10 :border-radius 5}
                              :on-press #(nav-to props "EmailList")}
           [text centered-white-bold "Sign up for Our Email List"]]]]))))

(defn staff-login-screen []
  (fn []
    (let [all-emails (<sub [:all-emails-submitted])]
      [view basic-column
       [view basic-row
        [text "Normally, you'd have to authenticate to see the staff backend, but this is a demo."]]
       [view basic-row
        [touchable-opacity {:style {:background-color "#999"
                                    :padding 10 :border-radius 5}
                            :on-press #(dispatch [:request-emails])}
         [text centered-white-bold "update list"]]]
       [view basic-row
        [text centered-list all-emails]]])))

(defn modal-screen []
  (let [greeting @(subscribe [:get-greeting])]
    (fn [props]
      [view basic-column
       [view basic-row
        [text greeting]]
       [view basic-row
        [text "Ingenuity is a Not-for-profit arts and technology incubator."]]
       [view basic-row
        [touchable-opacity {:style {:background-color "#999"
                                    :padding 10 :border-radius 5}
                            :on-press #(nav-go-url ingenuity-url)}
         [text centered-white-bold "Go to our website"]]]
       [view basic-row
        [touchable-opacity {:style {:background-color "#999"
                                    :padding 10 :border-radius 5}
                            :on-press #(nav-go-back props)}
         [text centered-white-bold "dismiss"]]]])))


(def main-stack
  (create-stack-navigator
   (clj->js {:Home {:screen (r/reactify-component home-screen)}
             :EmailList  {:screen (r/reactify-component collect-email-screen)}})))

(def home-stack
  (create-stack-navigator
   (clj->js {:Main        {:screen (r/reactify-component main-stack)}
             :Modal       {:screen (r/reactify-component modal-screen)}})
   (clj->js {:mode "modal"
             :headerMode "none"})))

(def tab-nav
  (create-tab-navigator
   (clj->js {:Home       {:screen (r/reactify-component home-stack)}
             :StaffLogin {:screen (r/reactify-component staff-login-screen)}})
   (clj->js {:tabBarOptions {:style {:backgroundColor "#eee"}}})))


(defn app-root [] [:> (create-app-container tab-nav) {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "ingenu" #(r/reactify-component app-root)))
