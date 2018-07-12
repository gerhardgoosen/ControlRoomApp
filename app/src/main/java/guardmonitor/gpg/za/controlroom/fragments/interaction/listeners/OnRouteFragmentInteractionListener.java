package guardmonitor.gpg.za.controlroom.fragments.interaction.listeners;



import guardmonitor.gpg.za.db.pojo.Route;


/**
    * This interface must be implemented by activities that contain this
            * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p/>
            * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
            * >Communicating with Other Fragments</a> for more information.
            */
    public interface OnRouteFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRouteFragmentInteraction(Route route);
    }